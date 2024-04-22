package awesoma.common.exceptions.commands;

import awesoma.common.exceptions.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.exceptions.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This commands shows elements of the collection
 */
public class Show extends Command {
    private final Vector<Movie> collection;

    public Show(Vector<Movie> collection) {
        super(
                "show",
                "This commands shows elements of the collection"
        );
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) {
        if (args.size() == argAmount) {
            System.out.println("[STORED DATA]: ");
            for (Movie m : collection) {
                System.out.println(m);
            }
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }
}
