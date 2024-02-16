package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.ArrayList;
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

//    public void execute(
//            TreeSet<Movie> collection,
//            long id,
//            String name,
//            Long oscarsCount,
//            Long totalBoxOffice,
//            double usaBoxOffice,
//            Long x, Float y, Date creationDate,
//            MovieGenre genre, String personName,
//            LocalDateTime birthday, Double weight,
//            Color eyeColor, Country nationality,
//            CommandManager commandManager
//    ) {

//        collection.add(new Movie(
//                        id,
//                        name,
//                        oscarsCount,
//                        totalBoxOffice,
//                        usaBoxOffice,
//                        new Coordinates(x, y),
//                        creationDate,
//                        genre,
//                        new Person(
//                                personName,
//                                birthday,
//                                weight,
//                                eyeColor,
//                                nationality
//                        )
//                )
//        );
//        commandManager.addToHistory(this);
//    }


    @Override
    public void execute(ArrayList<String> args, CommandManager commandManager) {

    }
}
