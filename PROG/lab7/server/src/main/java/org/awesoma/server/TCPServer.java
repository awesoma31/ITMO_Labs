package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.commands.Exit;
import org.awesoma.common.exceptions.EnvVariableNotFoundException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.server.exceptions.NoConnectionException;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TCPServer {
    private static final Logger logger = LogManager.getLogger(TCPServer.class);
    private static final String PATH = System.getenv(Environment.ENV);
    private final String host;
    private final int port;
    private CommandInvoker commandInvoker;
    private DumpManager dumpManager;
    private CollectionManager collectionManager;
    private boolean connectionClosing = false;

    public TCPServer(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            dumpManager = new DumpManager(PATH, new Validator());
            collectionManager = new CollectionManager(dumpManager);
            commandInvoker = new CommandInvoker(this);
        } catch (EnvVariableNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (ValidationException e) {
            System.err.println("Collection validation failed: " + e.getLocalizedMessage());
            commandInvoker.visit(new Exit());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            commandInvoker.visit(new Exit());
            System.exit(1);
        }
//        catch (NullPointerException e) {
////            commandInvoker.visit(new Exit());
//            System.err.println("File with data is probably empty");
//            System.exit(1);
//        }
    }

    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            logger.info("Server started");

            interactive(serverSocketChannel);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            logger.info("Server stopped");
        }
    }

    private void interactive(ServerSocketChannel serverSocketChannel) throws IOException {
        connectionClosing = false;
        while (!connectionClosing) {
            try (var selector = Selector.open()) {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                Response response = null;
                Request request;
                logger.info("Awaiting client");
                while (true) {
                    try {
                        selector.selectNow();
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
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
                                    commandInvoker.visit(new Exit());
                                    throw new NoConnectionException(e.getMessage());
                                } catch (IOException e) {
                                    commandInvoker.visit(new Exit());
                                    throw new NoConnectionException(e.getMessage());
                                }
                            } else if (key.isWritable()) {
                                SocketChannel clientChannel;
                                try {
                                    clientChannel = getSocketChannel(key);
                                    serializeThenSend(response, clientChannel);

                                    clientChannel.register(selector, SelectionKey.OP_READ);
                                } catch (NullPointerException e) {
                                    logger.error(e);
                                    commandInvoker.visit(new Exit());
                                    break;
                                }
                            }
                            keyIterator.remove();
                        }
                    } catch (NoConnectionException e) {
                        logger.error(e + " while selecting");
                        commandInvoker.visit(new Exit());
                        break;
                    }
                }
            } catch (ClosedChannelException e) {
                logger.error(e + " while processing");
                commandInvoker.visit(new Exit());
                break;
            } catch (IOException | RuntimeException e) {
                commandInvoker.visit(new Exit());
                logger.error(e);
                break;
            } finally {
                commandInvoker.visit(new Exit());
                logger.info("Selector closed");
            }
        }
    }

    private static SocketChannel getSocketChannel(SelectionKey key) throws IOException {
        var clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);
        return clientChannel;
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

        List<byte[]> parts = new ArrayList<>();
        var buffer = ByteBuffer.allocate(1024);
        int readBytesTotal = 0;
        int readBytes;
        while ((readBytes = clientChannel.read(buffer)) > 0) {
            buffer.flip();
            parts.add(new byte[readBytes]);
            buffer.get(parts.getLast(), 0, readBytes);
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

        var byteInputStream = new ByteArrayInputStream(result);
        var objIn = new ObjectInputStream(byteInputStream);

        var request = (Request) objIn.readObject();
        logger.info("Request accepted -> " + request.getCommandName());
        return request;

    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        try (var serverChannel = (ServerSocketChannel) key.channel()) {
            SocketChannel clientChannel = serverChannel.accept();
            logger.info("Client connected: " + clientChannel.getRemoteAddress());
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    private Response resolveRequest(Request request) {
        Command command = Environment.availableCommands.get(request.getCommandName());

        return command.accept(commandInvoker, request);
    }

    public void closeConnection() {
        connectionClosing = true;
    }

    public DumpManager getDumpManager() {
        return dumpManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }
}