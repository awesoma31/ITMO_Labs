package org.awesoma.common.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.Asker;
import org.awesoma.common.util.UniqueIdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * This command adds an element with given fields to the collection
 */
public class Add extends AbstractCommand {
    private final Vector<Movie> collection;
    private final UniqueIdGenerator idGenerator;


    public Add(UniqueIdGenerator idGenerator, Vector<Movie> collection) {
        super("add", "this command adds an element to the collection");
        this.idGenerator = idGenerator;
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount & this.reader != null) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Asker asker = new Asker(reader);
            int id = idGenerator.generateUniqueId();
            String name = asker.askName();
            double x = asker.askX();
            Long y = asker.askY();
            LocalDateTime creationDate = LocalDateTime.now();
            Integer oscarsCount = asker.askOscarsCount();
            int totalBoxOffice = asker.askTotalBoxOffice();
            Long usaBoxOffice = asker.askUsaBoxOffice();
            MovieGenre genre = asker.askGenre();
            String operatorName = asker.askOperatorName();
            Date birthdate = asker.askBirthdate();
            float weight = asker.askWeight();
            Color eyeColor = asker.askEyeColor();
            Country nationality = asker.askNationality();

            try {
                Person operator = new Person(operatorName, birthdate, weight, eyeColor, nationality);
                Coordinates coordinates = new Coordinates(x, y);
                Movie movie = new Movie(
                        id, name, oscarsCount, totalBoxOffice,
                        usaBoxOffice, coordinates, creationDate,
                        genre, operator
                );
                collection.add(movie);
            } catch (ValidationException e) {
                throw new CommandExecutingException(e.getMessage());
            }
        }
    }
}
