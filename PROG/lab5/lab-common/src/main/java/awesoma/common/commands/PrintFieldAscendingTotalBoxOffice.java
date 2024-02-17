package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class PrintFieldAscendingTotalBoxOffice extends Command {
    private final Vector<Movie> collection;
    private ArrayList<Integer> sortedData = new ArrayList<>();

    public PrintFieldAscendingTotalBoxOffice(Vector<Movie> collection) {
        super(
                "print_field_ascending_total_box_office",
                "Prints all values of totalBoxOffice in ascending order"
        );
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() == argAmount) {
            for (Movie m : collection) {
                sortedData.add(m.getTotalBoxOffice());
            }
            sortedData.sort(Integer::compareTo);
            System.out.print("[DATA]: ");
            System.out.println(sortedData);
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
