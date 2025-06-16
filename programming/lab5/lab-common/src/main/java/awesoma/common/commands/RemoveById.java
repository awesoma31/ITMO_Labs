package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This command deletes an element with given id from the collection
 */
public class RemoveById extends Command {
    protected static final int argAmount = 1;
    private final Vector<Movie> collection;

    public RemoveById(Vector<Movie> collection) {
        super("remove_by_id", "This command deletes an element with given id from the collection");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            try {
                int arg = Integer.parseInt(args.get(0));
                if (!collection.removeIf(m -> m.getId() == arg)) {
                    throw new CommandExecutingException("[FAIL]: Element with such id wasn't found in the collection");
                }
            } catch (NumberFormatException e) {
                throw new CommandExecutingException("[EXCEPTION]: Error while parsing an argument");
            }
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
