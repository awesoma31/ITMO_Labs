package org.awesoma;

import org.awesoma.common.Environment;
import org.awesoma.common.commands.AbstractCommand;
import org.awesoma.common.commands.Command;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;

class Client {
    private static int reconnectionAttempts = 5;
    private static final int maxReconnectionAttempts = 5;

    private final String host;
    private final int port;
    private SocketChannel clientChannel;
    private final long reconnectionTimeout = 5 * 1000;

    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        while (true) {
            try {
                clientChannel = SocketChannel.open(new InetSocketAddress(host, port));

                interactive();
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
                try {
                    Thread.sleep(reconnectionTimeout);
                } catch (InterruptedException ex) {
                    System.err.println("[ERROR]: while waiting to reconnect to the server");
                    System.exit(1);
                }
                reconnectionAttempts++;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void interactive() throws IOException, ClassNotFoundException {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        setReaders(consoleReader);

        String input;
        Command command;
        ObjectInputStream objIn = new ObjectInputStream(clientChannel.socket().getInputStream());
        ObjectOutputStream objOut = new ObjectOutputStream(clientChannel.socket().getOutputStream());
        while (true) {
            input = consoleReader.readLine();

            if (input == null) {
                System.exit(0);
            }

            input = input.trim();
            if (!input.isEmpty()) {
                String[] input_data = input.split(" ");
                String commandName = input_data[0];
                ArrayList<String> args = new ArrayList<String>(Arrays.asList(input_data).subList(1, input_data.length));

                try {
                    command = Environment.availableCommands.get(commandName);
                    Request request = command.buildRequest(args);
                    objOut.writeObject(request);
                } catch (NullPointerException e) {
                    System.err.println("[FAIL]: Command <" + commandName + "> not found");
                    continue;
                }

                Response response = (Response) objIn.readObject();
                command.handleResponse(response);
            }
        }
    }

    private static void setReaders(BufferedReader consoleReader) {
        for (String key : Environment.availableCommands.keySet()) {
            AbstractCommand command = (AbstractCommand) Environment.availableCommands.get(key);
            command.setDefaultReader(consoleReader);
            command.setReader(consoleReader);
        }
    }
}