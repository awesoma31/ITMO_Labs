package org.awesoma.client;

//import org.awesoma.common.UserCredentials;

import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.DataSerializer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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

    public void sendRegisterRequest(UserCredentials userCred) throws IOException, ClassNotFoundException {
        setUserCredentials(userCred);
        sendThenHandleResponse(getCommand("register"), new ArrayList<>());
    }

    public void sendLoginRequest(UserCredentials userCred) throws IOException, ClassNotFoundException {
        setUserCredentials(userCred);
        sendThenHandleResponse(getCommand("login"), new ArrayList<>());
    }

    private void sendThenHandleResponse(Command command, ArrayList<String> args) throws IOException {
        sendCommand(command, args);

        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    public void sendCommand(Command command, ArrayList<String> args) throws IOException {
        var request = command.buildRequest(args);
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

    private Command getCommand(String commandName) {
        return Environment.getAvailableCommands().get(commandName);
    }

    public void setUserCredentials(UserCredentials userCred) {
        this.userCredentials = userCred;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
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
