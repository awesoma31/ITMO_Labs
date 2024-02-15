package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterStartsWithName extends Command {
    public FilterStartsWithName() {
        super(
                "filterStartsWithName",
                "вывести элементы, значение поля name которых начинается с заданной подстроки"
        );
    }

    public void execute(TreeSet<Movie> collection, String s, CommandManager commandManager) {
//        Matcher matcher = pattern.
        boolean f = false;
        String regex = "^" + s;
        Pattern pattern = Pattern.compile(regex);
        for (Movie m : collection) {
//            Matcher matcher = pattern.matcher(m.getName());
//            System.out.println(matcher);
            if (m.getName().matches(regex)) {
                f = true;
                System.out.println(m);
            }
        }

        if (!f) {
            System.out.println("No matches found");
        }

        commandManager.addToHistory(this);
    }
}
