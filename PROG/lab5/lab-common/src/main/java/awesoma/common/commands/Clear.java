package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class Clear extends Command{
    public Clear() {
        super("clear", "This commands clears the collection");
    }

    public void execute(TreeSet<Movie> collection, CommandManager commandManager) {
        collection.clear();
        commandManager.addToHistory(this);
    }
}
