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
import java.util.List;

class Client {
    private final String host;
    private final int port;

    private SocketChannel clientChannel;

    private BufferedReader consoleReader;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;


    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        try {
            clientChannel = SocketChannel.open();
            clientChannel.connect(new InetSocketAddress(host, port));

            interactive();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void interactive() throws IOException, ClassNotFoundException {
        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        for (String key : Environment.availableCommands.keySet()) {
            AbstractCommand command = (AbstractCommand) Environment.availableCommands.get(key);
            // Установка поля
            command.setDefaultReader(consoleReader);
            command.setReader(consoleReader);
        }

        String input;
        Command command;
        objIn = new ObjectInputStream(clientChannel.socket().getInputStream());
        objOut = new ObjectOutputStream(clientChannel.socket().getOutputStream());
        while (true) {
            input = consoleReader.readLine();

            if (input == null) {
                System.exit(0);
            }

            input = input.trim();
            if (!input.isEmpty()) {
                String[] input_data = input.split(" ");
                String commandName = input_data[0];
                List<String> args = Arrays.asList(input_data).subList(1, input_data.length);

                try {
                    command = Environment.availableCommands.get(commandName);
                    Request request = command.buildRequest(args);
                    objOut.writeObject(request);
                } catch (NullPointerException e) {
                    System.err.println("[FAIL]: Command not found, try again...");
                    continue;
                }

                Response response = (Response) objIn.readObject();
                command.handleResponse(response);
            }
        }
    }
}