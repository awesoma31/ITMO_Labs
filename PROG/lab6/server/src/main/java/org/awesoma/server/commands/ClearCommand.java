package org.awesoma.server.commands;

import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class ClearCommand extends AbstractServerCommand implements Command {
    private final Vector<Movie> collection;

    public ClearCommand(String name, Vector<Movie> collection) {
        super(name);
        this.collection = collection;
    }

    private void clearCollection() {
        this.collection.clear();
    }

    @Override
    public void execute(ArrayList<String> args, Movie movie) {
        clearCollection();
    }
}
