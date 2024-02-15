package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TreeSet;

public class Update extends Command {
    public Update() {
        super("update", "this command updates fields of element with given id");
    }

    public void execute(TreeSet<Movie> collection, int id, Movie movie, CommandManager commandManager) {
        movie.setId(id);
        collection.add(movie);
        commandManager.addToHistory(this);
    }

    public void execute(
            TreeSet<Movie> collection,
            int id,
            String name,
            Integer oscarsCount,
            Long totalBoxOffice,
            Float usaBoxOffice,
            Long x, Float y, Date creationDate,
            MovieGenre genre, String personName,
            LocalDateTime birthday, Double weight,
            Color eyeColor, Country nationality,
            CommandManager commandManager
    ) {

        collection.add(new Movie(
                        id,
                        name,
                        oscarsCount,
                        totalBoxOffice,
                        usaBoxOffice,
                        new Coordinates(x, y),
                        creationDate,
                        genre,
                        new Person(
                                personName,
                                birthday,
                                weight,
                                eyeColor,
                                nationality
                        )
                )
        );
        commandManager.addToHistory(this);
    }
}
