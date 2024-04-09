package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final String PATH = "lab6";
    private final String host;
    private final int port;
    private final CommandInvoker commandInvoker;
    private final DumpManager dumpManager;
    private final CollectionManager collectionManager;
    private boolean connectionClosing = false;



    public Server(String host, int port) throws ValidationException, IOException {
        this.host = host;
        this.port = port;

        dumpManager = new DumpManager(System.getenv(PATH), new Validator());
        collectionManager = new CollectionManager(dumpManager);
        commandInvoker = new CommandInvoker(this);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            logger.info("Server started");

            while (true) {
                boolean selecting = true;
                connectionClosing = false;
                var selector = Selector.open();
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                Response response = null;
                Request request;
                logger.info("Awaiting client");
                while (selecting) {
                    selector.selectNow();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    if (connectionClosing) {
                        logger.info("Connection closed");
                        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                        selecting = false;
                    }
                    while (keyIterator.hasNext()) {
                        if (connectionClosing) {
                            selecting = false;
                            break;
                        }
                        SelectionKey key = keyIterator.next();
                        if (key.isAcceptable()) {
                            accept(key, selector);
                        } else if (key.isReadable()) {
                            try {
                                SocketChannel clientChannel = getSocketChannel(key);
                                request = receiveThenDeserialize(clientChannel);
                                response = resolveRequest(request);

                                if (connectionClosing) {
                                    clientChannel.close();
                                    selecting = false;
                                    break;
                                }

                                clientChannel.register(selector, SelectionKey.OP_WRITE);
                            } catch (ClassNotFoundException e) {
                                logger.error(e);
                                continue;
                            } catch (SocketException e) {
                                logger.info("Client disconnected");
                                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                                selecting = false;
                                break;
                            }
                        } else if (key.isWritable()) {
                            SocketChannel clientChannel = null;
                            try {
                                clientChannel = getSocketChannel(key);
                                serializeThenSend(response, clientChannel);

                                clientChannel.register(selector, SelectionKey.OP_READ);
                            } catch (NullPointerException e) {
                                if (connectionClosing) {
                                    assert clientChannel != null;
                                    clientChannel.close();
                                    selecting = false;
                                } else {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        keyIterator.remove();
                    }
                }
                selector.close();
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        try (var serverChannel = (ServerSocketChannel) key.channel()) {
            SocketChannel clientChannel;
            clientChannel = serverChannel.accept();
            logger.info("Client connected: " + clientChannel.getRemoteAddress());
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
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
        var readBuffer = ByteBuffer.allocate(65536);

        int bytesRead = clientChannel.read(readBuffer);
        var receivedData = new byte[readBuffer.remaining()];

        if (bytesRead == -1) {
            logger.error("data wasn't received");
            throw new RuntimeException();
        } else if (bytesRead == 0) {
            logger.error("received data is empty");
            throw new RuntimeException();
        } else {
            readBuffer.flip();
            int bytesToRead = Math.min(bytesRead, receivedData.length);
            readBuffer.get(receivedData, 0, bytesToRead);

            var byteInputStream = new ByteArrayInputStream(receivedData);
            var objIn = new ObjectInputStream(byteInputStream);

            var request = (Request) objIn.readObject();
            logger.info("Request accepted -> " + request.getCommandName());
            return request;
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