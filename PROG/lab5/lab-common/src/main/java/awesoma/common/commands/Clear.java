package awesoma.common.commands;

import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.Movie;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * this command clears the collection
 */
public class Clear extends Command {
    private final Vector<Movie> collection;
    private InputStream inputStream;

    public Clear(Vector<Movie> collection) {
        super("clear", "This commands clears the collection");
        this.collection = collection;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
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
