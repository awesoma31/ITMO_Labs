package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Abstract class that represents command being
 */
public abstract class AbstractCommand {
    protected static final int argAmount = 0;
    protected final String description;
    protected String name;
    protected BufferedReader defaultReader;
    protected BufferedReader reader;

    public AbstractCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void execute(ArrayList<String> args) throws CommandExecutingException {

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
}

