package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;
import awesoma.common.models.Person;

import java.util.TreeSet;

public class CountByOperator extends Command {
    public CountByOperator() {
        super(
                "countByOperator",
                "This command counts amount of movies whose operator equals given"
        );
    }

    public int execute(TreeSet<Movie> collection, Person operator, CommandManager commandManager) {
        int c = 0;

        for (Movie m : collection) {
            if (m.getOperator().equals(operator)) {
                c++;
            }
        }
        commandManager.addToHistory(this);
        return c;
    }
}
