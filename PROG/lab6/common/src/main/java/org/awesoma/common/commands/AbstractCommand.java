package org.awesoma.common.commands;

import org.awesoma.common.interaction.Response;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractCommand implements Command{

    protected final String name;
    protected final String description;



    public AbstractCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public Response execute(ArrayList<String> args) {
        return null;
    }

    @Override
    public Response execute(ArrayList<String> args, Movie movie) {
        this.execute(args);
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
