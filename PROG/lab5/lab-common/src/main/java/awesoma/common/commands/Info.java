package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

public class Info extends Command {
    public static final int argAmount = 0;
    private final Date initDate;
    private Vector<Movie> collection;

    public Info(Vector<Movie> collection, Date initDate) {
        super(
                "info",
                "This command shows info about current collection"
//                commandManager
        );
        this.initDate = initDate;
        this.collection = collection;
    }

    public void execute(TreeSet<Movie> collection, CommandManager commandManager) {
        System.out.println("Collection type is -> " + collection.getClass());
        System.out.println("Initialization time is - " + initDate);
        System.out.println("Amount of stored elements is - " + collection.size());
        commandManager.addToHistory(this);
    }

    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) {
        if (args.size() == argAmount) {
            System.out.println("Collection type is Vector<Movie>");
            // TODO дата инициализации
            System.out.println("Initialization time is - " + initDate);
            System.out.println("Amount of stored elements is - " + collection.size());
            commandManager.addToHistory(this);
        } else {
            throw new WrongAmountOfArgumentsException();
        }

    }
}