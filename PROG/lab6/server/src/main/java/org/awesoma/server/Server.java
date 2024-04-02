package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.commands.AbstractCommand;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private final String host;
    private final int port;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverChannel = new ServerSocket(port)) {
//            serverChannel.bind(new InetSocketAddress(host, port));
            Socket clientChannel = serverChannel.accept();
            objOut = new ObjectOutputStream(clientChannel.getOutputStream());
            objIn = new ObjectInputStream(clientChannel.getInputStream());


//            serverChannel.configureBlocking(false);
//            Selector selector = Selector.open();

//            SocketChannel clientChannel = serverSocket.accept();

//
//            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Server started");
            Request request;
            Response response;
            while (true) {
                request = (Request) objIn.readObject();
                response = AbstractCommand.availableCommands.get(request.getCommandName()).execute(request.getArgs());
                objOut.writeObject(response);
//                int readyChannels = selector.select();

//                if (readyChannels == 0) {
//                    continue;
//                }
//
//                Set<SelectionKey> selectedKeys = selector.selectedKeys();
//                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//
//                while (keyIterator.hasNext()) {
//                    SelectionKey key = keyIterator.next();
//
//                    if (key.isAcceptable()) accept(key, selector);
//                    else if (key.isReadable()) read(key);
////                    else if (key.isWritable()) write(key);
//
//                    keyIterator.remove();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocket.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.info("Client connected: " + clientChannel.getRemoteAddress());
    }

    private void read(SelectionKey key) throws IOException, ClassNotFoundException {
        // todo deserialize request
        // todo request.handle()
        // todo serialize response
        SocketChannel clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);
        ObjectInputStream objIn = new ObjectInputStream(clientChannel.socket().getInputStream());
        ObjectOutputStream objOut = new ObjectOutputStream(clientChannel.socket().getOutputStream());
        Request request = (Request) objIn.readObject();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        int bytesRead = clientChannel.read(buffer);

//        if (bytesRead == -1) {
//            logger.info("Client disconnected: " + clientChannel.getRemoteAddress());
//            clientChannel.close();
//            key.cancel();
//            return;
//        }

//        buffer.flip();
//        byte[] data = new byte[buffer.limit()];
//        buffer.get(data);
        String message;
        logger.info("Received from client " + clientChannel.getRemoteAddress() + ": " + request);

//        clientChannel.register(selector, SelectionKey.OP_WRITE);
            objOut.writeUTF(String.valueOf(request));
//        clientChannel.write(ByteBuffer.wrap(message.getBytes()));
    }

    private void write(SelectionKey key) {
        var clientChannel = (SocketChannel) key.channel();
        var buffer = ByteBuffer.allocate(1024);
    }
}