package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TreeSet;

public class Add extends Command {
    public Add() {
        super(
                "add",
                "This command add the element to the collection"
        );
    }

    public void execute(TreeSet<Movie> collection, Movie movie, CommandManager commandManager) {
        collection.add(movie);
        commandManager.addToHistory(this);
    }

    public void execute(
            TreeSet<Movie> collection,
            Integer id, String name, Integer oscarsCount,
            Long totalBoxOffice, Float usaBoxOffice,
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
