package org.awesoma.client;

import org.awesoma.client.commands.ExecuteScript;
import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.InfiniteScriptCallLoopException;
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

import static org.awesoma.common.util.DataSerializer.deserialize;

/**
 * Class that represents client
 */
class Client {
    private static final int maxReconnectionAttempts = 60;
    private static int reconnectionAttempts = 0;
    private final String host;
    private final int port;
    private final HashSet<String> usedPaths = new HashSet<>();
    private SocketChannel clientChannel;
    private UserCredentials userCredentials;

    Client(String host, int port) {
        this.host = host;
        this.port = port;

        registerCommands();
    }

    private static ArrayList<String> getArgs(String[] input_data) {
        return new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));
    }


    /**
     * receive data from channel
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

    private static void setReaders(BufferedReader reader) {
        for (String key : Environment.getAvailableCommands().keySet()) {
            Command command = Environment.getAvailableCommands().get(key);
            command.setDefaultReader(reader);
            command.setReader(reader);
        }
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


    /**
     * start channel
     */
    public void run() {
        while (true) {
            try {
                clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
                System.out.println("Connected to server: " + clientChannel.getRemoteAddress());

                interactive();
            } catch (IOException e) {
                if (failedToReconnect(e)) break;
            }
        }
    }


    /**
     * interactive IO communication with server
     * @throws IOException
     */
    @SuppressWarnings("all")
    private void interactive() throws IOException {
        var consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String username = askUsername(consoleReader);
        String password = String.valueOf(askPassword(consoleReader));
        userCredentials = new UserCredentials(username, password);
        System.out.println("-----------------");

        setReaders(consoleReader);

        String input;
        Command command;

        while (true) {
            input = consoleReader.readLine();

            checkNull(input);
            input = input.trim();

            if (!input.isEmpty()) {
                var input_data = input.split(" ");
                var commandName = input_data[0];
                var args = getArgs(input_data);

                try {
                    command = getCommand(commandName);
                    if (command instanceof ExecuteScript) {
                        executeScript(args, consoleReader);
                        continue;
                    }

                    try {
                        sendThenHandleResponse(command, args);
                    } catch (IOException e) {
                        System.err.println(e);
                        throw e;
                    } catch (ClassNotFoundException e) {
                        System.err.println(e);
                        throw new RuntimeException(e);
                    }
                } catch (NullPointerException e) {
                    System.err.println("[FAIL]: Command <" + commandName + "> not found");
                    continue;
                } catch (InfiniteScriptCallLoopException e) {
                    System.err.println("Infinite loop occurred: " + e.getMessage());
                    continue;
                } catch (CommandExecutingException e) {
                    System.err.println("Command execution exception: " + e.getMessage());
                    continue;
                }
            }
            setReaders(consoleReader);
        }
    }

    // ALERT!!! GOVNOCODE
    private void executeScript(ArrayList<String> args, BufferedReader initReader) throws CommandExecutingException {
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
                        Command command1 = getCommand(commandName);
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
                        System.err.println("Infinite loop occurred: " + e.getMessage());
                        break;
                    } catch (CommandExecutingException e) {
                        System.err.println("Command execution exception: " + e.getMessage());
                        break;
                    } catch (ClassNotFoundException e) {
                        System.err.println("Couldn't deserialize response from server");
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

    private String askUsername(BufferedReader consoleReader) throws IOException {
        String input;
        System.out.print("username: ");
        while (true) {
            input = consoleReader.readLine();
            checkNull(input);
            input = input.trim();
            if (!input.isEmpty()) {
                break;
            }
            System.err.println("Your username can't be empty!");
        }
        return input.trim();
    }

    private byte[] askPassword(BufferedReader reader) {
        System.out.print("password (enter to skip): ");
        try {
            var input = reader.readLine();
            checkNull(input);
            input = input.trim();

            return hashPassword(input);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] hashPassword(String p) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(p.getBytes());
    }

    private void sendThenHandleResponse(Command command, ArrayList<String> args) throws IOException, ClassNotFoundException {
        var request = command.buildRequest(args);
        request.setUserCredentials(userCredentials);

        var byteRequest = DataSerializer.serialize(request);

        var buffer = ByteBuffer.allocate(byteRequest.length);
        buffer.put(byteRequest);
        buffer.flip();

        clientChannel.write(buffer);

        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    private Response receiveThenDeserialize(SocketChannel clientChannel) throws IOException {
        var receivedData = receive(clientChannel);

        return deserialize(receivedData, Response.class);
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

    private Command getCommand(String commandName) {
        return Environment.getAvailableCommands().get(commandName);
    }

    private void registerCommands() {
        Environment.register(new ExecuteScript());
    }
}