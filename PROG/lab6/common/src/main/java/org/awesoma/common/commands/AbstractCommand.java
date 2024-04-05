package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.models.Coordinates;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.Person;
import org.awesoma.common.util.Asker;

import java.io.BufferedReader;
import java.util.ArrayList;

public abstract class AbstractCommand implements Command {
    protected final String name;
    protected final String description;
    protected BufferedReader defaultReader;
    protected BufferedReader reader;


    public AbstractCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

//    @Override
//    public abstract Response execute(ArrayList<String> args);

//    @Override
//    public Response execute(ArrayList<String> args, Movie movie) {
//        return this.execute(args);
//    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDefaultReader(BufferedReader defaultReader) {
        this.defaultReader = defaultReader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
