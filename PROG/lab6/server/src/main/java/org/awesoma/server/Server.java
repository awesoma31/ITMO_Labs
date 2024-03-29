package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
    private final String host;
    private final int port;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Server started");

            while (true) {
                // Ожидание готовности каналов
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                // Получение готовых ключей
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) accept(key, selector);
                    else if (key.isReadable()) read(key);
                    else if (key.isWritable()) write(key);

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocket.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.info("Client connected: " + clientChannel.getRemoteAddress());
    }

    private void read(SelectionKey key) throws IOException {
        // todo deserialize request
        // todo request.handle()
        // todo serialize response
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            logger.info("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            key.cancel();
            return;
        }

        buffer.flip();
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        String message = new String(data);
        logger.info("Received from client " + clientChannel.getRemoteAddress() + ": " + message);

        clientChannel.register(selector, SelectionKey.OP_WRITE);

        clientChannel.write(ByteBuffer.wrap(message.getBytes()));
    }

    private void write(SelectionKey key) {
        var clientChannel = (SocketChannel) key.channel();
        var buffer = ByteBuffer.allocate(1024);
    }
}