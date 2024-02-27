package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * this command shows info about current collection like amount od elements, initialization date and etc
 */
public class Info extends Command {
    public static final int argAmount = 0;
    private final Date initDate;
    private final Vector<Movie> collection;

    public Info(Vector<Movie> collection, Date initDate) {
        super(
                "info",
                "This command shows info about current collection"
        );
        this.initDate = initDate;
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) {
        if (args.size() == argAmount) {
            System.out.println("[COLLECTION INFO:]");
            System.out.println("Collection type is Vector<Movie>");
            System.out.println("Initialization time is - " + initDate);
            System.out.println("Amount of stored elements is - " + collection.size());
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
