package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * this command clears the collection
 */
public class Clear extends Command {
    private final Vector<Movie> collection;

    public Clear(Vector<Movie> collection) {
        super("clear", "This commands clears the collection");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) {
        if (args.size() == argAmount) {
            collection.clear();
            System.out.println("[INFO]: collection cleared successfully");
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
