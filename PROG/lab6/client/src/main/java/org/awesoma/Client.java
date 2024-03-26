package org.awesoma;

import org.awesoma.commands.*;
//import org.awesoma.common.commands.*;
import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
import org.awesoma.common.exceptions.*;

import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * main class that represents user
 *
 * @author awesoma31
 */
public final class Client {
    private final String host;
    private final int port;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    private HashMap<String, AbstractClientCommand> availableCommands;
    private BufferedReader consoleReader;
    private Socket clientSocket;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public void run() throws IOException {
        availableCommands = new HashMap<>();

        System.out.println("connecting to server");
        clientSocket = new Socket(host, port);
        System.out.println("connected");

        consoleReader = new BufferedReader(new InputStreamReader(System.in));

        serverReader = new ObjectInputStream(clientSocket.getInputStream());
        serverWriter = new ObjectOutputStream(clientSocket.getOutputStream());


        Add add = new Add(serverWriter, serverReader);
        AddIfMax addIfMax = new AddIfMax(serverWriter, serverReader);
        add.setDefaultReader(consoleReader);
        add.setReader(consoleReader);
        addIfMax.setDefaultReader(consoleReader);
        addIfMax.setReader(consoleReader);


        availableCommands.put("show", new Show(serverWriter, serverReader));
        availableCommands.put("add", add);
        availableCommands.put("add_if_max", addIfMax);

        // todo setServerWritersAndReaders(serverWriter, serverReader)

        System.out.println("starting client console");
        interactiveMode();
        System.out.println("exited interactive mode");

    }

    void interactiveMode() {
        String consoleInput;
        while (true) {
            try {
                consoleInput = consoleReader.readLine();

                if (consoleInput == null || (consoleInput.equals("q")) || (consoleInput.equals("exit"))) {
                    serverReader.close();
                    serverWriter.close();
                    clientSocket.close();
                    consoleReader.close();
                    System.exit(0);
                }

                if (!consoleInput.isEmpty()) {
                    try {
                        String[] input_data = consoleInput.split(" ");
                        String commandName = input_data[0];
                        AbstractClientCommand command = availableCommands.get(commandName);
                        ArrayList<String> args = new ArrayList<>(Arrays.asList(input_data).subList(1, input_data.length));

                        Response serverResponse = command.execute(args);

//                        System.out.println(serverResponse);
//                        System.out.println(serverResponse.getExtraData());

                        if (serverResponse.getStatusCode() == StatusCode.ERROR) {
                            System.err.println("[FAIL]: server responded with status code: <" + serverResponse.getStatusCode() + ">: cause: " + serverResponse.getMessage());
                        }
//                        else if (serverResponse.getStatusCode() == StatusCode.OK) {
//                            System.out.println("[INFO]: server response: " + serverResponse.getStatusCode() + ", " + serverResponse.getMessage() + " executed successfully");
//                        }
                    } catch (CommandExecutingException e) {
                        System.err.println("execution fail");
                    } catch (WrongAmountOfArgumentsException e) {
                        System.err.println(e.getMessage());
                    } catch (NullPointerException e) {
                        System.err.println("[FAIL]: This command is not recognised");
                    }
                }
            } catch (WrongAmountOfArgumentsException | CommandExecutingException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                System.err.println("[FAIL]: This command is not recognised");
            }
        }

    }

    public void setAvailableCommands(HashMap<String, AbstractClientCommand> availableCommands) {
        this.availableCommands = availableCommands;
    }

    public HashMap<String, AbstractClientCommand> getAvailableCommands() {
        return availableCommands;
    }
}
