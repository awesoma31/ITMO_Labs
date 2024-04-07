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
import java.net.SocketException;

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
        Request request;
        Response response;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket client = serverSocket.accept();

                try {
                    logger.info("Server started");
                    // todo переделать на селектор
                    // обернуть bytearayinputstream внутрь objout, затем bytebuffer.wrap(bytes)
                    objOut = new ObjectOutputStream(client.getOutputStream());
                    objIn = new ObjectInputStream(client.getInputStream());

                    logger.info("Client connected: " + client.getRemoteSocketAddress());

                    while (true) {
                        request = (Request) objIn.readObject();
                        logger.info("Request accepted: command -> " + request.getCommandName());

                        response = resolveRequest(request);
                        objOut.writeObject(response);
                        logger.info("Response sent: status -> " + response.getStatusCode());
                    }
                } catch (SocketException e ) {
                    client.close();
                    logger.info(e.getLocalizedMessage() + " - " + " Client disconnected");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private Response resolveRequest(Request request) {
        Command command = Environment.availableCommands.get(request.getCommandName());
        return command.accept(commandInvoker, request);
    }
}