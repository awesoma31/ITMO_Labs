package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * this command prints usaBoxOffice values of collection elements in descending order
 */
public class PrintFieldDescendingUsaBoxOffice extends Command {
    private final Vector<Movie> collection;
    private final ArrayList<Long> sortedData = new ArrayList<>();

    public PrintFieldDescendingUsaBoxOffice(Vector<Movie> collection) {
        super(
                "print_field_descending_usa_box_office",
                "Prints all values of usaBoxOffice in descending order"
        );
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            for (Movie m : collection) {
                sortedData.add(m.getUsaBoxOffice());
            }
            Comparator<Long> comparator = Collections.reverseOrder();

            sortedData.sort(comparator);
            System.out.print("[DATA]: ");
            System.out.println(sortedData);
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
