package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This command removes element from the collection by given index
 */
public class RemoveAt extends AbstractCommand {
    protected static final int argAmount = 1;
    private final Vector<Movie> collection;

    public RemoveAt(Vector<Movie> collection) {
        super("remove_at", "This command removes element from the collection by given index");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            try {
                int index = Integer.parseInt(args.get(0));
                collection.remove(index);
            } catch (NumberFormatException e) {
                throw new CommandExecutingException("[EXCEPTION]: Error while parsing an argument");
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new CommandExecutingException("[EXCEPTION]: No element in collection with such index");
            }
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
