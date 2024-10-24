package org.awesoma.client;

//import org.awesoma.common.UserCredentials;

import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;
import org.awesoma.common.commands.Command;
import org.awesoma.common.commands.ExecuteScript;
import org.awesoma.common.commands.LoginCommand;
import org.awesoma.common.commands.RegisterCommand;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.InfiniteScriptCallLoopException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.DataSerializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import static org.awesoma.common.util.DataSerializer.deserialize;

/**
 * Class that represents client
 */
public class Client {
    private static final int maxReconnectionAttempts = 60;
    private static int reconnectionAttempts = 0;
    private final String host;
    private final int port;
    private final HashSet<String> usedPaths = new HashSet<>();
    private SocketChannel clientChannel;
    private UserCredentials userCredentials;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static ArrayList<String> getArgs(String[] input_data) {
        return new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));
    }

    /**
     * receive data from channel
     *
     * @param clientChannel which receives data
     * @return byte array
     */
    private static byte[] receive(SocketChannel clientChannel) throws IOException {
        var responseBuffer = ByteBuffer.allocate(65536);
        clientChannel.read(responseBuffer);

        int bytesRead = responseBuffer.position();
        var receivedData = new byte[bytesRead];

        responseBuffer.flip();
        responseBuffer.get(receivedData);
        return receivedData;
    }

    private static void checkNull(String input) {
        if (input == null) {
            System.exit(0);
        }
    }

    private static void checkFile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new CommandExecutingException("File not file");
        } else if (!file.exists()) {
            throw new CommandExecutingException("File doesn't exist");
        } else if (!file.canRead()) {
            throw new CommandExecutingException("Can't read file");
        } else if (file.isDirectory()) {
            throw new CommandExecutingException("Can't execute directory");
        }
    }

    public void executeScript(ArrayList<String> args, BufferedReader initReader) throws CommandExecutingException {
        var path = args.get(0);
        checkFile(path);

        try (var fis = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            setReaders(fis);
            while ((line = fis.readLine()) != null) {
                if (!line.isEmpty()) {
                    var input_data = line.split(" ");
                    var commandName = input_data[0];
                    var commandArgs = getArgs(input_data);

                    try {
                        var command1 = getCommand(commandName);
                        if (command1 instanceof ExecuteScript) {
                            if (usedPaths.contains(path)) {
                                usedPaths.clear();
                                throw new InfiniteScriptCallLoopException();
                            } else {
                                usedPaths.add(path);
                                executeScript(commandArgs, fis);
                                continue;
                            }
                        }

                        sendThenHandleResponse(command1, commandArgs);
                    } catch (NullPointerException e) {
                        System.err.println("[FAIL]: Command <" + commandName + "> not found");
                    } catch (InfiniteScriptCallLoopException e) {
                        throw new CommandExecutingException("Infinite loop occurred: " + e.getMessage());
                    } catch (CommandExecutingException e) {
                        throw new CommandExecutingException(e.getMessage());
                    }
                }
            }
            setReaders(initReader);
        } catch (FileNotFoundException e) {
            throw new CommandExecutingException("Script with such name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setReaders(BufferedReader reader) {
        for (String key : Environment.getAvailableCommands().keySet()) {
            Command command = Environment.getAvailableCommands().get(key);
            command.setDefaultReader(reader);
            command.setReader(reader);
        }
    }

    public void openSocket() {
        while (true) {
            try {
                clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
                System.out.println("Connected to server: " + clientChannel.getRemoteAddress());
                return;
            } catch (IOException e) {
                if (failedToReconnect(e)) return;
            }
        }
    }

    private boolean failedToReconnect(IOException e) {
        if (reconnectionAttempts > maxReconnectionAttempts) {
            System.err.println("Maximum reconnection attempts reached");
            return true;
        }
        if (reconnectionAttempts % 5 == 0) {
            System.err.println(e.getMessage());
        }
        try {
            long reconnectionTimeout = 300;
            Thread.sleep(reconnectionTimeout);
        } catch (InterruptedException ex) {
            System.err.println("[ERROR]: while waiting to reconnect to the server");
            System.exit(1);
        }
        reconnectionAttempts++;
        return false;
    }

    public Response sendThenGetResponse(Command command, ArrayList<String> args, Movie movie) throws IOException {
        sendCommand(command, args, movie);
        var r = receiveResponse();
        command.handleResponse(r);
        return r;
    }

    public Response sendThenGetResponse(Command command, ArrayList<String> args) throws IOException {
        sendCommand(command, args);
        var r = receiveResponse();
        command.handleResponse(r);
        return r;
    }

    public void sendRegisterRequest(UserCredentials userCred) throws IOException, ClassNotFoundException {
        setUserCredentials(userCred);
        sendThenHandleResponse(getCommand(RegisterCommand.NAME), new ArrayList<>());
    }

    public void sendLoginRequest(UserCredentials userCred) throws IOException, ClassNotFoundException {
        setUserCredentials(userCred);
        sendThenHandleResponse(getCommand(LoginCommand.NAME), new ArrayList<>());
    }

    public void sendThenHandleResponse(Command command, ArrayList<String> args) throws IOException {
        sendCommand(command, args);

        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    public void sendThenHandleResponse(Command command, ArrayList<String> args, Movie movie) throws IOException {
        sendCommand(command, args, movie);
        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    public Response receiveResponse() throws IOException {
        return receiveThenDeserialize(clientChannel);
    }

    public void sendCommand(Command command, ArrayList<String> args) throws IOException {
        var request = command.buildRequest(args);
        sendRequest(request);
    }

    public String hashPassword(String p) {
        // todo to client
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(p.getBytes());
            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCommand(Command command, ArrayList<String> args, Movie movie) throws IOException {
        var request = command.buildRequest(args, movie);
        sendRequest(request);
    }

    private void sendRequest(Request request) throws IOException {
        request.setUserCredentials(userCredentials);

        var byteRequest = DataSerializer.serialize(request);

        var buffer = ByteBuffer.allocate(byteRequest.length);
        buffer.put(byteRequest);
        buffer.flip();

        clientChannel.write(buffer);
    }

    private Response receiveThenDeserialize(SocketChannel clientChannel) throws IOException {
        var receivedData = receive(clientChannel);

        return deserialize(receivedData, Response.class);
    }

    public Command getCommand(String commandName) {
        return Environment.getAvailableCommands().get(commandName);
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCred) {
        this.userCredentials = userCred;
    }

    public Vector<Movie> getCollectionFromDB() {
        try {
            var c = getCommand("show");
            // ноль понимания почему первый респонс пустой приходит, но костыль работает
            sendCommand(c, new ArrayList<>());
            receiveThenDeserialize(clientChannel);

            sendCommand(c, new ArrayList<>());
            var response = receiveThenDeserialize(clientChannel);
            var col = response.getCollection();
            return col;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
