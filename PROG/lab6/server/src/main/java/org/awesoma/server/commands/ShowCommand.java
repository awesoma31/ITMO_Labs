package org.awesoma.server.commands;

import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class ShowCommand extends AbstractServerCommand {
    private final Vector<Movie> collection;
    public ShowCommand(Vector<Movie> collection) {
        super("show");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args, Movie movie) {

    }
}
