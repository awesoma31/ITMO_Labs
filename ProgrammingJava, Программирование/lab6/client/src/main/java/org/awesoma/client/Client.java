package org.awesoma.client;

import org.awesoma.client.commands.ExecuteScript;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.InfiniteScriptCallLoopException;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.DataSerializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.awesoma.common.util.DataSerializer.deserialize;

class Client {
    private static final int maxReconnectionAttempts = 15;
    private static int reconnectionAttempts = 0;
    private final String host;
    private final int port;
    private final HashSet<String> usedPaths = new HashSet<>();
    private SocketChannel clientChannel;

    Client(String host, int port) {
        this.host = host;
        this.port = port;

        registerCommands();
    }

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

    @SuppressWarnings("all")
    private void interactive() throws IOException {
        System.out.println("-----------------");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        setReaders(consoleReader);

        String input;
        Command command;

        while (true) {
            input = consoleReader.readLine();

            checkNull(input);
            input = input.trim();

            if (!input.isEmpty()) {
                String[] input_data = input.split(" ");
                String commandName = input_data[0];
                ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                try {
                    command = Environment.getAvailableCommands().get(commandName);
                    if (command instanceof ExecuteScript) {
                        executeScript(args, consoleReader);
                        continue;
                    }

                    sendThenHandleResponse(command, args);
                } catch (NullPointerException e) {
                    System.err.println("[FAIL]: Command <" + commandName + "> not found");
                    continue;
                } catch (InfiniteScriptCallLoopException e) {
                    System.err.println("Infinite loop occurred: " + e.getMessage());
                    continue;
                } catch (CommandExecutingException e) {
                    System.err.println("Command execution exception: " + e.getMessage());
                    continue;
                } catch (ClassNotFoundException e) {
                    System.err.println("Couldn't deserialize response from server");
                    continue;
                }
            }
            setReaders(consoleReader);
        }
    }

    private void executeScript(ArrayList<String> args, BufferedReader initReader) throws CommandExecutingException {
        var path = args.get(0);
        checkFile(path);

        try (var fis = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            setReaders(fis);
            while ((line = fis.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] input_data = line.split(" ");
                    String commandName = input_data[0];
                    ArrayList<String> commandArgs = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                    try {
                        Command command1 = Environment.getAvailableCommands().get(commandName);
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

    private void sendThenHandleResponse(Command command, ArrayList<String> args) throws IOException, ClassNotFoundException {
        var request = command.buildRequest(args);
        var byteRequest = DataSerializer.serialize(request);

        var buffer = ByteBuffer.allocate(byteRequest.length);
        buffer.put(byteRequest);
        buffer.flip();

        clientChannel.write(buffer);

        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    // ALERT!!! GOVNOCODE
    private Response receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        var receivedData = receive(clientChannel);
        return deserialize(receivedData, Response.class);
    }

    private boolean failedToReconnect(IOException e) {
        if (reconnectionAttempts > maxReconnectionAttempts) {
            System.err.println("Maximum reconnection attempts reached");
            return true;
        }
        System.err.println(e.getLocalizedMessage());
        reconnect();
        return false;
    }

    private static void reconnect() {
        try {
            long reconnectionTimeout = 1000;
            Thread.sleep(reconnectionTimeout);
        } catch (InterruptedException ex) {
            System.err.println("[ERROR]: while waiting to reconnect to the server");
            System.exit(1);
        }
        reconnectionAttempts++;
    }

    private void registerCommands() {
        Environment.register(new ExecuteScript());
    }
}