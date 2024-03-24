package org.awesoma.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.exceptions.CommandExecutingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Abstract class that represents command being
 */
public abstract class AbstractClientCommand {
    protected static final int argAmount = 0;
    protected final String description;
    protected String name;
    protected BufferedReader defaultReader;
    protected BufferedReader reader;
    protected ObjectOutputStream serverWriter;
    protected ObjectInputStream serverReader;
    protected Response response;

//    private Request request;

    public AbstractClientCommand(String name, String description, ObjectInputStream serverReader, ObjectOutputStream serverWriter) {
        this.name = name;
        this.description = description;
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
    }

    public abstract Response execute(ArrayList<String> args) throws CommandExecutingException, IOException;

    protected Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        serverWriter.writeObject(request);
        serverWriter.flush();

        return (Response) serverReader.readObject();
    }


    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public BufferedReader getDefaultReader() {
        return defaultReader;
    }

    public void setDefaultReader(BufferedReader defaultReader) {
        this.defaultReader = defaultReader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getInfo() {
        return "Command name: " + this.name + ";\n Description: " + this.description;
    }

    public void setServerWriter(ObjectOutputStream serverWriter) {
        this.serverWriter = serverWriter;
    }

    public void setServerReader(ObjectInputStream serverReader) {
        this.serverReader = serverReader;
    }
}

