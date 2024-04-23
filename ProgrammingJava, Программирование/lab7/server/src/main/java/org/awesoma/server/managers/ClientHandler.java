package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
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



    public ClientHandler(CommandInvoker commandInvoker, SocketChannel clientChannel) throws FileNotFoundException {
        this.commandInvoker = commandInvoker;
        this.clientChannel = clientChannel;
        try {
            this.db = new DBManager();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request;
                request = receiveThenDeserialize(clientChannel);
                db.addUser(request.getUserCredentials());

                cashedPool.execute( () -> {
                    Command command = Environment.getAvailableCommands().get(request.getCommandName());
                    var response = command.accept(commandInvoker, request);

                    sendResponse(response);
                });
            } catch (EOFException ignored) {

            } catch (SocketException e) {
                logger.info("Client disconnected");
                return;
            } catch (StreamCorruptedException e) {
                logger.error("thread: " + e + Thread.currentThread().getName());
                return;
            } catch (IOException | ClassNotFoundException e) {
                logger.error("thread: " + e + Thread.currentThread().getName());
                e.printStackTrace();
                break;
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }

    private void sendResponse(Response response) {
        forkJoinPool.execute(() -> {
            try {
                logger.info("Response -> " + response.getStatusCode() + " (client: " + clientChannel.getRemoteAddress() + ")");
                serializeThenSend(response, clientChannel);
            } catch (IOException e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        });
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

    private synchronized Request receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        var request = deserialize(receiveBytes(clientChannel), Request.class);
        logger.info("Request -> " + request.getCommandName() + " (client: " + clientChannel.getRemoteAddress() + ")");
        return request;
    }

    private byte[] receiveBytes(SocketChannel clientChannel) throws IOException {
//        ArrayList<byte[]> parts = new ArrayList<>();
//        // todo StreamCorruptedException из за того что маленький буфер или из-за того что большой
//        var buffer = ByteBuffer.allocate(516);
//        int readBytesTotal = 0;
//        int readBytes;
//        while ((readBytes = clientChannel.read(buffer)) > 0) {
//            buffer.flip();
//            parts.add(new byte[readBytes]);
//            buffer.get(parts.get(parts.size() - 1), 0, readBytes);
//            buffer.flip();
//            readBytesTotal += readBytes;
//        }
//
//        if (readBytesTotal == -1) {
//            logger.error("Data wasn't received: " + clientChannel.getRemoteAddress());
//        }
//
//        var result = new byte[readBytesTotal];
//        var resultIdx = 0;
//
//        for (var part : parts) {
//            System.arraycopy(part, 0, result, resultIdx, part.length);
//            resultIdx += part.length;
//        }
//        return result;
        try {
            var receiveBuffer = ByteBuffer.allocate(65536);
            clientChannel.read(receiveBuffer);
            receiveBuffer.flip();
            var data = new byte[receiveBuffer.remaining()];
            receiveBuffer.get(data);
            return data;
        } catch (EOFException e) {
            logger.error("" + e.getMessage() + (e.getCause() == null ? e.getCause() : ""));
            throw new RuntimeException(e);
        }
    }

    private Response resolveRequest(Request request) throws IOException {
        Command command = Environment.getAvailableCommands().get(request.getCommandName());
        var response = command.accept(commandInvoker, request);
        logger.info("Response -> " + response.getStatusCode() + " (client: " + clientChannel.getRemoteAddress() + ")");
        return response;
    }
}
