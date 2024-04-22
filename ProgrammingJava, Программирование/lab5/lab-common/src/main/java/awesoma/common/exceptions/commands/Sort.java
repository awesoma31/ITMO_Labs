package awesoma.common.exceptions.commands;

import awesoma.common.exceptions.exceptions.CommandExecutingException;
import awesoma.common.exceptions.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.exceptions.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This command sorts the collection by movie id
 */
public class Sort extends Command {
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
