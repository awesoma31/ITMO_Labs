package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.models.Movie;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.Vector;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    private final String host;
    private final int port;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private final CollectionManager collectionManager;
    private final Vector<Movie> collection;
    private final CommandInvoker commandInvoker;


    public Server(String host, int port) {
        this.host = host;
        this.port = port;

        this.collectionManager = new CollectionManager();
        this.collection = collectionManager.getCollection();
        this.commandInvoker = new CommandInvoker(collectionManager, this);
    }

    public void run() {
        try (ServerSocket serverChannel = new ServerSocket(port)) {
            logger.info("Server started");
            Socket clientChannel = serverChannel.accept();
            objOut = new ObjectOutputStream(clientChannel.getOutputStream());
            objIn = new ObjectInputStream(clientChannel.getInputStream());

            logger.info("Client connected: " + clientChannel.getInetAddress());
            Request request;
            Response response;

            while (true) {
                request = (Request) objIn.readObject();
                logger.info("Request accepted: command -> " + request.getCommandName());

                response = resolveRequest(request);
                objOut.writeObject(response);
                logger.info("Response sent: status -> " + response.getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Response resolveRequest(Request request) {
        Command command = Environment.availableCommands.get(request.getCommandName());
        return command.accept(commandInvoker, request);
    }
}