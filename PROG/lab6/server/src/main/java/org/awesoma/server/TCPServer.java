package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.EnvVariableNotFoundException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Validator;
import org.awesoma.server.util.json.DumpManager;
import org.awesoma.server.exceptions.NoConnectionException;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static org.awesoma.common.util.DataSerializer.deserialize;

public class TCPServer {
    public static final String DB_PASSWD = "1";
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    private static final Logger logger = LogManager.getLogger(TCPServer.class);
    private static final String PATH = System.getenv(Environment.ENV);
    private final String host;
    private final int port;
    private CommandInvoker commandInvoker;
    private DumpManager dumpManager;
    private CollectionManager collectionManager;
    private Connection dbConnection;

    public TCPServer(String host, int port) {
        this.host = host;
        this.port = port;


        registerCommands();
        try {
            dumpManager = new DumpManager(PATH, new Validator());
            collectionManager = new CollectionManager(dumpManager);
            commandInvoker = new CommandInvoker(this);
        } catch (EnvVariableNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (ValidationException e) {
            System.err.println("Collection validation failed: " + e.getLocalizedMessage());
            commandInvoker.visit(new ExitCommand());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            commandInvoker.visit(new ExitCommand());
            System.exit(1);
        }
    }

    private static SocketChannel getSocketChannel(SelectionKey key) throws IOException {
        var clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);
        return clientChannel;
    }

    private static byte[] receiveBytes(SocketChannel clientChannel) throws IOException {
        ArrayList<byte[]> parts = new ArrayList<>();
        var buffer = ByteBuffer.allocate(128);
        int readBytesTotal = 0;
        int readBytes;
        while ((readBytes = clientChannel.read(buffer)) > 0) {
            buffer.flip();
            parts.add(new byte[readBytes]);
            buffer.get(parts.get(parts.size() - 1), 0, readBytes);
            buffer.flip();
            readBytesTotal += readBytes;
        }

        if (readBytesTotal == -1) {
            logger.error("data wasn't received");
            throw new RuntimeException();
        } else if (readBytesTotal == 0) {
            logger.error("received data is empty");
            throw new RuntimeException();
        }

        var result = new byte[readBytesTotal];
        var resultIdx = 0;

        for (var part : parts) {
            System.arraycopy(part, 0, result, resultIdx, part.length);
            resultIdx += part.length;
        }
        return result;
    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        try (var serverChannel = (ServerSocketChannel) key.channel()) {
            SocketChannel clientChannel = serverChannel.accept();
            logger.info("Client connected: " + clientChannel.getRemoteAddress());
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            logger.info("Server started");
//            DBConnect();

            interactive(serverSocketChannel);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            logger.info("Server stopped");
        }
    }

    private void DBConnect() {
        try {
            dbConnection = DriverManager.getConnection(DB_URL, USER, DB_PASSWD);
            logger.info("DB connection sustained successfully");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    private void DBDisconnect() {
        try {
            dbConnection.close();
            logger.info("DB connection closed");
        } catch (SQLException | NullPointerException e) {
            logger.error("Error trying ti disconnect from DB" + e);
            throw new RuntimeException(e);
        }
    }

    private void interactive(ServerSocketChannel serverSocketChannel) throws IOException {
        mainLoop:
        while (true) {
            try (var selector = Selector.open()) {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                Response response = null;
                Request request;
                logger.info("Awaiting client");
                selectingLoop:
                while (true) {
                    try {
                        selector.selectNow();
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                        keyIterationLoop:
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isAcceptable()) {
                                accept(key, selector);
                            } else if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel = getSocketChannel(key);
                                    request = receiveThenDeserialize(clientChannel);
                                    response = resolveRequest(request);
                                    clientChannel.register(selector, SelectionKey.OP_WRITE);
                                } catch (ClassNotFoundException e) {
                                    logger.error(e);
                                    continue;
                                } catch (SocketException e) {
                                    logger.info("Client disconnected");
                                    commandInvoker.visit(new ExitCommand());
                                    break selectingLoop;
//                                    throw new NoConnectionException(e.getMessage());
                                } catch (IOException e) {
                                    commandInvoker.visit(new ExitCommand());
                                    break selectingLoop;
//                                    throw new NoConnectionException(e.getMessage());
                                }
                            } else if (key.isWritable()) {
                                SocketChannel clientChannel;
                                try {
                                    clientChannel = getSocketChannel(key);
                                    serializeThenSend(response, clientChannel);

                                    clientChannel.register(selector, SelectionKey.OP_READ);
                                } catch (NullPointerException e) {
                                    logger.error(e);
//                                    commandInvoker.visit(new Exit());
                                    break selectingLoop;
                                }
                            }
                            keyIterator.remove();
                        }
                    } catch (NoConnectionException e) {
                        logger.error(e + " while selecting");
                        commandInvoker.visit(new ExitCommand());
                        break;
                    }
                }
            } catch (ClosedChannelException e) {
                logger.error(e + " while processing");
                commandInvoker.visit(new ExitCommand());
                break;
            } catch (IOException | RuntimeException e) {
                commandInvoker.visit(new ExitCommand());
                logger.error(e);
                break;
            } finally {
                commandInvoker.visit(new ExitCommand());
                logger.info("Selector closed");
            }
        }
    }

    private void serializeThenSend(Response response, SocketChannel clientChannel) throws IOException {
        assert response != null;
        var byteStream = new ByteArrayOutputStream();
        var objOut = new ObjectOutputStream(byteStream);
        objOut.writeObject(response);
        objOut.flush();

        byte[] serializedData = byteStream.toByteArray();

        var writeBuffer = ByteBuffer.allocate(serializedData.length);
        writeBuffer.put(serializedData);
        writeBuffer.flip();
        clientChannel.write(writeBuffer);

        logger.info("Response sent: -> " + response.getStatusCode());
    }

    private Request receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {

//        var result = receiveBytes(clientChannel);

//        var byteInputStream = new ByteArrayInputStream(result);
//        var objIn = new ObjectInputStream(byteInputStream);
//
//        var request = (Request) objIn.readObject();

        var request = deserialize(receiveBytes(clientChannel), Request.class);
        logger.info("Request accepted -> " + request.getCommandName());
        return request;

    }

    private Response resolveRequest(Request request) {
        Command command = Environment.getAvailableCommands().get(request.getCommandName());
        return command.accept(commandInvoker, request);
    }

    public void closeConnection() {
        DBDisconnect();
    }

    public DumpManager getDumpManager() {
        return dumpManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    private void registerCommands() {
        Environment.register(new HelpCommand());
        Environment.register(new ShowCommand());
        Environment.register(new ExitCommand());
        Environment.register(new AddCommand());
        Environment.register(new InfoCommand());
        Environment.register(new ClearCommand());
        Environment.register(new SortCommand());
        Environment.register(new PrintFieldAscendingTBOCommand());
        Environment.register(new UpdateIdCommand());
        Environment.register(new RemoveByIdCommand());
        Environment.register(new RemoveAtCommand());
        Environment.register(new AddIfMaxCommand());
    }
}