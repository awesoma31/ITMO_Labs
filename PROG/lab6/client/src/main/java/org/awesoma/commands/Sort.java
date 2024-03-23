package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This command sorts the collection by movie id
 */
public class Sort extends AbstractCommand {
    private final Vector<Movie> collection;

    public Sort(Vector<Movie> collection) {
        super("sort", "This command sorts the collection by Movie.id");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            collection.sort(Movie::compareTo);
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
