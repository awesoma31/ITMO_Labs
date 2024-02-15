package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class RemoveGreater extends Command{
    public RemoveGreater() {
        super(
                "removeGreater",
                "This command removes all elements from the collection whose ID is greater than given value"
        );
    }

    public TreeSet<Movie> execute(TreeSet<Movie> collection, int id, CommandManager commandManager) {
        for (Movie m:collection) {
            if (m.getId()==id+1) {
                commandManager.addToHistory(this);
                return (TreeSet<Movie>) collection.headSet(m);
            }
        }
        commandManager.addToHistory(this);
        return collection;
    }
}
