package org.awesoma.server.commands;

import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class AddCommand extends AbstractServerCommand {
    private Vector<Movie> collection;

    public AddCommand() {
        super("add");
    }
    public AddCommand(Vector<Movie> collection) {
        super("add");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args, Movie movie){
        // todo
        collection.add(movie);
    }

    public void setCollection(Vector<Movie> collection) {
        this.collection = collection;
    }
}
