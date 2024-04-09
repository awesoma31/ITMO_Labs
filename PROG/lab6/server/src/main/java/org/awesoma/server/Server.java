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
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;


    public Server(String host, int port) throws ValidationException, IOException {
        this.host = host;
        this.port = port;

        Validator validator = new Validator();
        DumpManager dumpManager = new DumpManager(System.getenv(PATH), validator);
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        this.commandInvoker = new CommandInvoker(collectionManager, dumpManager);
    }

    public void run() {
//        Request request;
//        Response response;

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            var selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Server started");

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                Response response = null;
                Request request;

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
//                            throw new RuntimeException(e);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel clientChannel = getSocketChannel(key);
                        serializeThenSend(response, clientChannel);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    }
                    keyIterator.remove();
                }

//                SocketChannel client = serverSocketChannel.accept();
//
//                try {
//                    // todo переделать на селектор
//                    // обернуть bytearayinputstream внутрь objout, затем bytebuffer.wrap(bytes)
////                    objOut = new ObjectOutputStream(client.getOutputStream());
////                    objIn = new ObjectInputStream(client.getInputStream());
//
//                    logger.info("Client connected: " + client.getRemoteAddress());
//
//                    while (true) {
//                        byte[] receivedData = new byte[65536];
//                        ByteBuffer buffer = ByteBuffer.allocate(65536);
//                        client.read(buffer);
//                        buffer.flip();
//                        int rem = buffer.remaining();
//                        buffer.get(receivedData);
//
//                        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(receivedData);
//                        objIn = new ObjectInputStream(byteInputStream);
//
//                        request = (Request) objIn.readObject();
//                        logger.info("Request accepted: command -> " + request.getCommandName());
//
//                        response = resolveRequest(request);
//
//                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//                        objOut = new ObjectOutputStream(byteStream);
//                        objOut.writeObject(response);
//                        objOut.flush();
//
//                        byte[] serializedData = byteStream.toByteArray();
//
//                        // Помещаем сериализованные данные в ByteBuffer
//                        ByteBuffer buffer1 = ByteBuffer.allocate(serializedData.length);
//                        buffer1.put(serializedData);
//                        buffer1.flip();
//                        client.write(buffer1);
//
//                        logger.info("Response sent: status -> " + response.getStatusCode());
//                    }
//                } catch (SocketException e) {
//                    client.close();
//                    logger.info(e.getLocalizedMessage() + " - " + " Client disconnected");
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        logger.info("Client connected: " + clientChannel.getRemoteAddress());
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private static SocketChannel getSocketChannel(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);
        return clientChannel;
    }

    private void serializeThenSend(Response response, SocketChannel clientChannel) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        objOut = new ObjectOutputStream(byteStream);
        objOut.writeObject(response);
        objOut.flush();

        byte[] serializedData = byteStream.toByteArray();

        ByteBuffer writeBuffer = ByteBuffer.allocate(serializedData.length);
        writeBuffer.put(serializedData);
        writeBuffer.flip();
        clientChannel.write(writeBuffer);

        logger.info("Response sent: status -> " + response.getStatusCode());
    }

    private Request receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        byte[] receivedData = new byte[65536];
        ByteBuffer readBuffer = ByteBuffer.allocate(65536);

        clientChannel.read(readBuffer);
//        readBuffer.flip();
        readBuffer.get(receivedData);

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(receivedData);
        objIn = new ObjectInputStream(byteInputStream);

        Request request = (Request) objIn.readObject();

        logger.info("Request accepted: command -> " + request.getCommandName());
        return request;
    }

    private Response resolveRequest(Request request) {
        Command command = Environment.availableCommands.get(request.getCommandName());
        return command.accept(commandInvoker, request);
    }
}