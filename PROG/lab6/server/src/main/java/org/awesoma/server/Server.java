package org.awesoma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.Command;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.server.managers.CollectionManager;
import org.awesoma.server.managers.CommandInvoker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            logger.info("Server started");
            Socket clientChannel = serverSocket.accept();
            // todo переделать на селектор
            // обернуть bytearayinputstream внутрь objout, затем bytebuffer.wrap(bytes)
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
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private Response resolveRequest(Request request) {
        Command command = Environment.availableCommands.get(request.getCommandName());
        return command.accept(commandInvoker, request);
    }
}