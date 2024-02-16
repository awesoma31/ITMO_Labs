package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class Sort extends Command {
    private final Vector<Movie> collection;

    public Sort(Vector<Movie> collection) {
        super("sort", "This command sorts the collection by Movie.id");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) throws CommandExecutingException {
        if (args.size() == argAmount) {
            collection.sort(Movie::compareTo);
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
