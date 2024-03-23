package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.MovieGenre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

/**
 * this command prints genre values of collection elements in descending order
 */
public class PrintFieldDescendingGenre extends AbstractCommand {
    private final Vector<Movie> collection;
    private final ArrayList<MovieGenre> data = new ArrayList<>();

    public PrintFieldDescendingGenre(Vector<Movie> collection) {
        super(
                "print_field_descending_genre",
                "This command prints all genre fields in descending order (alphabetic)"
        );
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            for (Movie m : collection) {
                data.add(m.getGenre());
            }
            System.out.println(data);
            data.sort(Comparator.naturalOrder());
            System.out.println(data);
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
