package org.awesoma.common.commands;

import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This commands shows elements of the collection
 */
public class Show extends AbstractCommand {
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
