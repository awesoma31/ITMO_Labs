package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class AddIfMax extends Command {
    public AddIfMax() {
        super(
                "addIfMax",
                "This command adds the element to the collection if its ID is maximum"
//                commandManager
        );
    }

    public void execute(Movie element, TreeSet<Movie> collection, CommandManager commandManager) {
        for (Movie m : collection) {
            if (m.getId() > element.getId()) {
                commandManager.addToHistory(this);
                return;
            }
        }
        collection.add(element);
        commandManager.addToHistory(this);
    }
}
