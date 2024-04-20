package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.commands.ExitCommand;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static org.awesoma.common.util.DataSerializer.deserialize;
import static org.awesoma.common.util.DataSerializer.serialize;

public class ClientHandler implements Runnable {
    //    private final SelectionKey key;
//    private final Selector selector;
    private final CommandInvoker commandInvoker;
    private final Logger logger = LogManager.getLogger(ClientHandler.class);
    private final SocketChannel clientChannel;
    private final Selector selector;


    public ClientHandler(CommandInvoker commandInvoker, SocketChannel clientChannel, Selector selector) {
        this.commandInvoker = commandInvoker;
        this.clientChannel = clientChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        Request request;
        Response response;
        while (true) {
            try {
                request = receiveThenDeserialize(clientChannel);
                response = resolveRequest(request);
                logger.info("Response -> " + response.getStatusCode());
                serializeThenSend(response, clientChannel);
                logger.info("Response sent");
            } catch (EOFException e) {
                logger.error(e);
            } catch (StreamCorruptedException e) {
                logger.error(e);
                return;
            }

            catch (SocketException e) {
//                logger.error("Client disconnected");
                return;
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e);
            }
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

    private synchronized Request receiveThenDeserialize(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        var request = deserialize(receiveBytes(clientChannel), Request.class);
        logger.info("Request accepted -> " + request.getCommandName() + " (client: " + clientChannel.getRemoteAddress() + ")");
        return request;
    }

    private byte[] receiveBytes(SocketChannel clientChannel) throws IOException {
        ArrayList<byte[]> parts = new ArrayList<>();
        // todo StreamCorruptedException из за того что маленький буфер или из-за того что большой
        var buffer = ByteBuffer.allocate(516);
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
            logger.error("Data wasn't received: " + clientChannel.getRemoteAddress());
        }

        var result = new byte[readBytesTotal];
        var resultIdx = 0;

        for (var part : parts) {
            System.arraycopy(part, 0, result, resultIdx, part.length);
            resultIdx += part.length;
        }
        return result;
    }

    private Response resolveRequest(Request request) throws IOException {
        Command command = Environment.getAvailableCommands().get(request.getCommandName());
        var response = command.accept(commandInvoker, request);
        logger.info("Response -> " + response.getStatusCode() + " (client: " + clientChannel.getRemoteAddress() + ")");
        return response;
    }
}
