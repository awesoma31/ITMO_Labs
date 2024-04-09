package org.awesoma;

import org.awesoma.common.Environment;
import org.awesoma.common.commands.AbstractCommand;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.InfiniteScriptCallLoopException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

class Client {
    private static final int maxReconnectionAttempts = 15;
    private static int reconnectionAttempts = 0;
    private final String host;
    private final int port;
    private final long reconnectionTimeout = 1000;
    private final ExecuteScript executeScript;
    private SocketChannel clientChannel;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private HashSet<String> usedPaths = new HashSet<>();

    Client(String host, int port) {
        this.host = host;
        this.port = port;
        executeScript = new ExecuteScript();
    }

    private static void setReaders(BufferedReader consoleReader) {
        for (String key : Environment.availableCommands.keySet()) {
            AbstractCommand command = (AbstractCommand) Environment.availableCommands.get(key);
            command.setDefaultReader(consoleReader);
            command.setReader(consoleReader);
        }
    }

    public void run() {
        Environment.availableCommands.put(ExecuteScript.NAME, executeScript);

        while (true) {
            try {
                clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
                System.out.println("Connected to server: " + clientChannel.getRemoteAddress());
                System.out.println("-----------------");

                interactive();
            } catch (IOException e) {
                if (reconnectionAttempts > maxReconnectionAttempts) {
                    System.err.println("Maximum reconnection attempts reached");
                    break;
                }
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
        Command command = null;
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
                ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                try {
                    command = Environment.availableCommands.get(commandName);
                    if (command instanceof ExecuteScript) {
                        executeScript(args, consoleReader, objIn, objOut);
                        continue;
                    }
                    Request request = command.buildRequest(args);
                    objOut.writeObject(request);
                } catch (NullPointerException e) {
                    System.err.println("[FAIL]: Command <" + commandName + "> not found");
                    continue;
                } catch (InfiniteScriptCallLoopException e) {
                    System.err.println(e.getMessage());
                } catch (CommandExecutingException e) {
                    System.err.println("Command execution exception: " + e.getMessage());
                    continue;
                }

                Response response = (Response) objIn.readObject();

                assert command != null;
                command.handleResponse(response);
            }
            setReaders(consoleReader);
        }
    }

    // ALERT!!! GOVNOCODE
    private void executeScript(ArrayList<String> args, BufferedReader defaultReader, ObjectInputStream objIn, ObjectOutputStream objOut) throws InfiniteScriptCallLoopException {
        String path = args.get(0);
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

        try (var fis = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line_;
            setReaders(fis);
            while ((line_ = fis.readLine()) != null) {
                try {
                    if (!line_.isEmpty()) {
                        String[] input_data = line_.split(" ");
                        String commandName_ = input_data[0];
                        ArrayList<String> args_ = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                        try {
                            Command command_ = Environment.availableCommands.get(commandName_);
                            if (command_ instanceof ExecuteScript) {
                                if (usedPaths.contains(path)) {
                                    usedPaths.clear();
                                    throw new InfiniteScriptCallLoopException();
                                } else {
                                    usedPaths.add(path);
                                    // todo
                                    executeScript(args_, fis, objIn, objOut);
                                    continue;
                                }
                            }
                            Request request = command_.buildRequest(args_);
                            objOut.writeObject(request);
                            Response response = (Response) objIn.readObject();
                            command_.handleResponse(response);
                        } catch (NullPointerException e) {
                            throw new CommandExecutingException("Command not found");
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("[FAIL]: This command is not recognised: it may be not registered or it doesn't exist");
                } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                    System.out.println(e.getMessage());
                } catch (FileNotFoundException e) {
                    System.err.println("File not found");
                    return;
                }
            }
            setReaders(defaultReader);
        } catch (FileNotFoundException e) {
            throw new CommandExecutingException("Script with such name not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}