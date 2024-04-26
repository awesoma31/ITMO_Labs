package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import static org.awesoma.common.util.DataSerializer.deserialize;
import static org.awesoma.common.util.DataSerializer.serialize;

public class ClientHandler implements Runnable {
    private final CommandInvoker commandInvoker;
    private final Logger logger = LogManager.getLogger(ClientHandler.class);
    private final SocketChannel clientChannel;
    private final ExecutorService cashedPool = Executors.newCachedThreadPool();
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final DBManager db;


    public ClientHandler(CommandInvoker commandInvoker, SocketChannel clientChannel) {
        this.commandInvoker = commandInvoker;
        this.clientChannel = clientChannel;
        try {
            this.db = new DBManager();
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            logger.error("SQLException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                var request = receiveThenDeserialize(clientChannel);
                db.addUser(request.getUserCredentials());

                cashedPool.execute(() -> {
                    var command = Environment.getAvailableCommands().get(request.getCommandName());
                    var response = command.accept(commandInvoker, request);

                    forkJoinPool.execute(() -> sendResponse(response));
                });
            } catch (EOFException ignored) {
            } catch (SocketException e) {
                logger.info("Client disconnected");
                return;
            } catch (IOException | ClassNotFoundException e) {
                logger.error("thread: " + Thread.currentThread().getName() + ":" + e.getMessage());
                return;
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    private void sendResponse(Response response) {
        try {
            logger.info("Response -> " + response.getStatusCode() + " (client: " + clientChannel.getRemoteAddress() + ")");
            serializeThenSend(response, clientChannel);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void serializeThenSend(Response response, SocketChannel clientChannel) throws IOException {
        writeBytes(clientChannel, serialize(response));
    }

    private void writeBytes(SocketChannel clientChannel, byte[] serializedData) throws IOException {
        var writeBuffer = ByteBuffer.allocate(serializedData.length);
        writeBuffer.put(serializedData);
        writeBuffer.flip();
        clientChannel.write(writeBuffer);
    }

    private Request receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        var request = deserialize(receiveBytes(clientChannel), Request.class);
        logger.info("Request -> " + request.getCommandName() + " (client: " + clientChannel.getRemoteAddress() + ")");
        return request;
    }

    private byte[] receiveBytes(SocketChannel clientChannel) throws IOException {
        try {
            var receiveBuffer = ByteBuffer.allocate(65536);
            clientChannel.read(receiveBuffer);
            receiveBuffer.flip();
            var data = new byte[receiveBuffer.remaining()];
            receiveBuffer.get(data);
            return data;
        } catch (EOFException e) {
            logger.error(e.getMessage() + (e.getCause() == null ? e.getCause() : ""));
            throw e;
        }
    }
}
