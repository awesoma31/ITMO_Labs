package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;
import awesoma.common.util.json.DumpManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Save extends Command {
    private final Vector<Movie> collection;
    private final DumpManager dumpManager;


    public Save(Vector<Movie> collection, DumpManager dumpManager) {
        super("save", "This command saves collection to the file");
        this.collection = collection;
        this.dumpManager = dumpManager;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            try {
                dumpManager.writeCollection(collection);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new CommandExecutingException("Exception while trying to write collection to file");
            }
        }
    }
}
