package org.awesoma.server.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class AddIfMaxCommand extends AbstractServerCommand{
    private Vector<Movie> collection;

    public AddIfMaxCommand() {
        super("add");
    }
    public AddIfMaxCommand(Vector<Movie> collection) {
        super("add");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args, Movie movie) throws CommandExecutingException {
        if (movie.getTotalBoxOffice() > maxTBO()) {
            collection.add(movie);
        }
    }

    private int maxTBO() {
        int max = 0;
        for (Movie m : collection) {
            if (m.getTotalBoxOffice() > max) {
                max = m.getTotalBoxOffice();
            }
        }
        return max;
    }
}
