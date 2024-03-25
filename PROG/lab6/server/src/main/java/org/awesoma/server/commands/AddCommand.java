package org.awesoma.server.commands;

import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
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
    public Response execute(ArrayList<String> args, Movie movie){
        if (collection.add(movie)) {
            return new Response(StatusCode.OK);
        }
        return new Response(StatusCode.ERROR, "element was not added");
    }

    public void setCollection(Vector<Movie> collection) {
        this.collection = collection;
    }
}
