package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;

public class Info extends Command {
    public Info() {
        super(
                "info",
                "This command shows info about current collection"
//                commandManager
        );
    }

    public void execute(TreeSet<Movie> collection, CommandManager commandManager) {
        System.out.println("Collection type is TreeSet<Movie>");
        // TODO дата инициализации
        System.out.println("Initialization time is - ");
        System.out.println("Amount of stored elements is - " + collection.size());
        commandManager.addToHistory(this);
    }
}
