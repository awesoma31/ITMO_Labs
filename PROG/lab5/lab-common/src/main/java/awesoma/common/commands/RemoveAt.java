package awesoma.common.commands;

import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.Vector;

public class RemoveAt extends Command {
    public static final int argAmount = 1;
    private final Vector<Movie> collection;

    public RemoveAt(Vector<Movie> collection) {
        super("remove_at", "This command removes element from the collection by given index");
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) throws CommandExecutingException {
        if (args.size() == argAmount) {
            try {
                int index = Integer.parseInt(args.get(0));
                collection.remove(index);
            } catch (NumberFormatException e) {
                throw new CommandExecutingException("[EXCEPTION]: Error while parsing an argument");
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new CommandExecutingException("[EXCEPTION]: List index is out of range");
            }
        } else {
            throw new WrongAmountOfArgumentsException();
        }
    }
}
