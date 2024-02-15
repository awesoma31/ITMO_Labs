package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class RemoveById extends Command{
    public RemoveById() {
        super(
                "removeById",
                "This command removes element with given id from collection"
//                commandManager
        );
    }


    public void execute(TreeSet<Movie> collection, int movieId, CommandManager commandManager) {
        collection.removeIf(m -> m.getId() == movieId);
        commandManager.addToHistory(this);
    }
}
