package org.awesoma.common;

import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class Environment {
    public static final HashMap<String, Command> availableCommands = new HashMap<>();
    public static final Vector<Movie> collection = new Vector<>();

    // todo

    static {
        try {
            collection.add(
                    new Movie(
                            1,
                            "Mamba",
                            20,
                            30,
                            43L,
                            new Coordinates(3, 4),
                            LocalDateTime.now(),
                            MovieGenre.COMEDY,
                            new Person(
                                    "Jopik",
                                    new Date(),
                                    34f,
                                    Color.RED,
                                    Country.FRANCE
                            )
                    )
            );
            collection.add(
                    new Movie(
                            2,
                            "Jango",
                            40,
                            20,
                            54L,
                            new Coordinates(2, 1),
                            LocalDateTime.now(),
                            MovieGenre.COMEDY,
                            new Person(
                                    "Jopik",
                                    new Date(),
                                    34f,
                                    Color.RED,
                                    Country.FRANCE
                            )
                    )
            );
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        availableCommands.put("help", new Help());
        availableCommands.put("show", new Show());
        availableCommands.put("exit", new Exit());
        availableCommands.put("add", new Add());
        availableCommands.put("info", new Info());
        availableCommands.put("clear", new Clear());
        availableCommands.put("sort", new Sort());
    }
}
