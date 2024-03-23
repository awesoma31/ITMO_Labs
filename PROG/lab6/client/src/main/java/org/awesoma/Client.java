package org.awesoma.client;

import org.awesoma.commands.*;
//import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.EnvVariableNotFoundException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.UniqueIdGenerator;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.Console;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 * main class that represents user
 *
 * @author awesoma31
 */
public final class Client {
    private static final String ENV = "lab6";
    private static final Date initDate = new Date();

    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
//    private UserHandler userHandler;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;

    public Client(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
//        this.userHandler = userHandler;
    }

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    /**
     * main method
     */
//    public static void main(String[] args) throws ValidationException {
//        String serverName = "localhost";
//        int port = 1821;
//
//        try {
//            BufferedReader defReader = new BufferedReader(new InputStreamReader(System.in));
//            Validator validator = new Validator();
//            DumpManager dumpManager = new DumpManager(System.getenv(ENV), validator);
////            Vector<Movie> collection = dumpManager.readCollection();
//            Vector<Movie> collection = new Vector<>(Arrays.asList(
//                    new Movie(1, "Jopa", 123, 321, 423L,
//                            new Coordinates(3D, 5L), LocalDateTime.now(), MovieGenre.COMEDY,
//                            new Person("Jota", new Date(1000), 90f, Color.RED, Country.FRANCE)
//                    ),
//                    new Movie(2, "Mamba", 312, 20, 645L,
//                            new Coordinates(3D, 5L), LocalDateTime.now(), MovieGenre.COMEDY,
//                            new Person("Jota", new Date(1000), 90f, Color.RED, Country.FRANCE)
//                    )
//            ));
//            UniqueIdGenerator idGenerator = new UniqueIdGenerator(UniqueIdGenerator.identifyIds(collection));
//
//            Help help = new Help();
//            Info info = new Info(collection, initDate);
//            Show show = new Show(collection);
//            Exit exit = new Exit();
//            Quit quit = new Quit();
//            Clear clear = new Clear(collection);
//            RemoveAt removeAt = new RemoveAt(collection);
//            RemoveById removeById = new RemoveById(collection);
//            Sort sort = new Sort(collection);
//            PrintFieldAscendingTotalBoxOffice printFieldAscendingTotalBoxOffice =
//                    new PrintFieldAscendingTotalBoxOffice(collection);
//            PrintFieldDescendingUsaBoxOffice printFieldDescendingUsaBoxOffice =
//                    new PrintFieldDescendingUsaBoxOffice(collection);
//            PrintFieldDescendingGenre printFieldDescendingGenre =
//                    new PrintFieldDescendingGenre(collection);
//            Add add = new Add(idGenerator, collection);
//            UpdateId updateId = new UpdateId(collection, defReader);
//            AddIfMax addIfMax = new AddIfMax(idGenerator, collection);
//            Save save = new Save(collection, dumpManager);
//            ExecuteScript executeScript = new ExecuteScript();
//
//            org.awesoma.commands.AbstractCommand[] commandsToReg = {
//                    help,
//                    info,
//                    show,
//                    exit,
//                    quit,
//                    clear,
//                    removeAt,
//                    removeById,
//                    sort,
//                    printFieldAscendingTotalBoxOffice,
//                    printFieldDescendingUsaBoxOffice,
//                    printFieldDescendingGenre,
//                    add,
//                    updateId,
//                    addIfMax,
//                    save,
//                    executeScript
//            };
//            for (AbstractCommand c : commandsToReg) {
//                c.setDefaultReader(defReader);
//                c.setReader(defReader);
//            }
//
//            Console console = new Console(
//                    commandsToReg,
//                    defReader
//            );
//            help.setRegisteredCommands(console.getRegisteredCommands());
//            executeScript.setRegisteredCommands(console.getRegisteredCommands());
//
//
//            connectToServer(serverName, port);
//
//            console.interactiveMode();
//
//        } catch (DateTimeParseException e) {
//            System.err.println("Exception while trying to validate creation time: " + e.getMessage());
//            System.exit(1);
//        }
////        catch (ConnectException e) {
////            System.err.println("sfs");
////        }
////        catch (IOException e) {
////            System.err.println("IOException caught, exiting the program");
////            System.exit(1);
////        }
//        catch (EnvVariableNotFoundException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
////        catch (ValidationException e) {
////            System.err.println(e.getMessage());
////            System.exit(1);
////        }
//    }


    public void run() {
        try {
            boolean processingStatus = true;
            while (processingStatus) {
                try {
                    connectToServer();
                    processingStatus = processRequestToServer();
                } catch (Exception exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectToServer(){
        try {
            if (reconnectionAttempts >= 1) {

            }
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));

            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean processRequestToServer() {

        return false;
    }
}
