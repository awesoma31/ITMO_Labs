package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class Show extends Command {
    public Show() {
        super("show", "This command prints all elements of collection");
    }

    public void execute(TreeSet<Movie> collection, CommandManager commandManager) {
        for (Movie el : collection) {
            System.out.println(el);
        }
        commandManager.addToHistory(this);
    }
}
