package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;
import awesoma.common.models.Person;

import java.util.TreeSet;

public class CountLessThanOscarsCount extends Command{
    public CountLessThanOscarsCount() {
        super(
                "countLessThanOscarsCount",
                "This command counts amount of elements whose oscarsCount is less than given value"
        );
    }

    public int execute(TreeSet<Movie> collection, Long oscarsCount, CommandManager commandManager) {
        int c = 0;

        for (Movie m : collection) {
            if (m.getOscarsCount() < oscarsCount) {
                c++;
            }
        }
        commandManager.addToHistory(this);
        return c;
    }
}
