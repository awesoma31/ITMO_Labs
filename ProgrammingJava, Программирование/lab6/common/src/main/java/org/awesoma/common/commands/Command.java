package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;

import java.io.BufferedReader;
import java.util.ArrayList;

public abstract class Command {
    protected final String name;
    protected final String description;
    protected BufferedReader defaultReader;
    protected BufferedReader reader;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public abstract Request buildRequest(ArrayList<String> args);

    public void handleResponse(Response response) {
        if (response.getStatusCode() == Status.OK) {
            if (response.getMessage() != null) {
                System.out.println(response.getMessage());
            }
        } else {
            System.out.println("[" + response.getStatusCode() + "]: " + response.getMessage());
        }
    }

    public abstract Response accept(CommandVisitor visitor, Request request);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHelp() {
        return "<" + this.getName() + ">: " + this.getDescription();
    }

    public void setDefaultReader(BufferedReader defaultReader) {
        this.defaultReader = defaultReader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
