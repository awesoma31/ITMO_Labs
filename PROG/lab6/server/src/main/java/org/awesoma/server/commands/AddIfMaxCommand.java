package org.awesoma.server.commands;

import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
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
    public Response execute(ArrayList<String> args, Movie movie) throws CommandExecutingException {
        if (movie.getTotalBoxOffice() > maxTBO()) {
            collection.add(movie);
            return new Response(StatusCode.OK);
        }
        return new Response(StatusCode.OK, "Movie wasn't added to the collection because itd TBO is not max");
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
