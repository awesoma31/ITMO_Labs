package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.util.json.DumpManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This command saves collection to the file which stated in env path
 */
public class Save extends AbstractCommand {
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
