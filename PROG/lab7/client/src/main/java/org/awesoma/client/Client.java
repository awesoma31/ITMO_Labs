package org.awesoma.client;

import org.awesoma.commands.ExecuteScript;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.AbstractCommand;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.InfiniteScriptCallLoopException;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.awesoma.common.util.Deserializer.deserialize;

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

    private static byte[] receive(SocketChannel clientChannel) throws IOException {
        var responseBuffer = ByteBuffer.allocate(65536);
        clientChannel.read(responseBuffer);

        int bytesRead = responseBuffer.position();
        var receivedData = new byte[bytesRead];

        responseBuffer.flip();
        responseBuffer.get(receivedData);
        return receivedData;
    }

    private static byte[] serialize(Request request) throws IOException {
        var byteOut = new ByteArrayOutputStream();
        var objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(request);
        objOut.flush();
        return byteOut.toByteArray();
    }

    private static void setReaders(BufferedReader consoleReader) {
        for (String key : Environment.getAvailableCommands().keySet()) {
            AbstractCommand command = (AbstractCommand) Environment.getAvailableCommands().get(key);
            command.setDefaultReader(consoleReader);
            command.setReader(consoleReader);
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

    private void interactive() throws IOException {
        System.out.println("-----------------");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        setReaders(consoleReader);

        String input;
        AbstractCommand command;

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

    private void sendThenHandleResponse(AbstractCommand command, ArrayList<String> args) throws IOException, ClassNotFoundException {
        var request = command.buildRequest(args);
        var byteRequest = serialize(request);

        var buffer = ByteBuffer.allocate(byteRequest.length);
        buffer.put(byteRequest);
        buffer.flip();

        clientChannel.write(buffer);

        command.handleResponse(receiveThenDeserialize(clientChannel));
    }

    // ALERT!!! GOVNOCODE
    private void executeScript(ArrayList<String> args, BufferedReader reader) throws CommandExecutingException {
        var path = args.get(0);
        checkFile(path);

        // todo
        try (var fis = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            setReaders(fis);
            while ((line = fis.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] input_data = line.split(" ");
                    String commandName = input_data[0];
                    ArrayList<String> commandArgs = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                    try {
                        AbstractCommand command1 = Environment.getAvailableCommands().get(commandName);
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
            setReaders(reader);
        } catch (FileNotFoundException e) {
            throw new CommandExecutingException("Script with such name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
//        e.printStackTrace();
        try {
            long reconnectionTimeout = 1000;
            Thread.sleep(reconnectionTimeout);
        } catch (InterruptedException ex) {
            System.err.println("[ERROR]: while waiting to reconnect to the server");
            System.exit(1);
        }
        reconnectionAttempts++;
        return false;
    }

    private void registerCommands() {
        Environment.register(new ExecuteScript());
    }
}