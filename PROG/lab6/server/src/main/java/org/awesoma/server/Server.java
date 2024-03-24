package org.awesoma.server;

//import org.awesoma.commands.*;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.models.*;
//import org.awesoma.common.util.Console;
import org.awesoma.server.commands.AbstractServerCommand;
import org.awesoma.server.commands.AddCommand;
import org.awesoma.server.commands.AddIfMaxCommand;
import org.awesoma.server.commands.ShowCommand;
import org.awesoma.server.util.CommandInvoker;
import org.awesoma.server.util.RequestReader;
import org.awesoma.server.util.ResponseSender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class Server{
    private int port = 8000;
    private int soTimeout = 0;
    private ServerSocket serverSocket;
    private RequestReader requestReader;
    private static final String ENV = "lab6";
//    private static final Date initDate = new Date();
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private CommandInvoker commandInvoker;
    private final Vector<Movie> collection;
    private ResponseSender responseSender;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(0);
        collection = new Vector<>();
    }

    public void run() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(port);

        requestReader = new RequestReader(objIn);
        responseSender = new ResponseSender(objOut);
        commandInvoker = new CommandInvoker(
                collection,
                new ShowCommand(collection),
                new AddCommand(collection),
                new AddIfMaxCommand(collection)
        );

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("server started");

            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());

            requestReader.setObjIn(objIn);
            responseSender.setObjOut(objOut);

            interactiveMode();

        }
    }

    private void interactiveMode() throws IOException, ClassNotFoundException {
        System.out.println("starting server console");
        while (true) {
           Request request = requestReader.readRequest();
           Response serverResponse = commandInvoker.invoke(request);
           responseSender.sendResponse(serverResponse);}
    }
}

