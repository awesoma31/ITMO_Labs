package awesoma.common.commands;

import awesoma.common.exceptions.*;
import awesoma.common.models.*;
import awesoma.common.util.Asker;
import awesoma.common.util.UniqueIdGenerator;
import awesoma.common.util.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This command adds an element with given fields to the collection
 */
public class Add extends Command {
    private final Vector<Movie> collection;
    private final UniqueIdGenerator idGenerator;
    public HashSet<Integer> idList;


    public Add(UniqueIdGenerator idGenerator, Vector<Movie> collection) {
        super("add", "this command adds an element to the collection");
        this.idList = idGenerator.getIdList();
        this.idGenerator = idGenerator;
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount & this.reader != null) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Asker asker = new Asker(reader);
            // id
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

            Person operator = new Person(operatorName, birthdate, weight, eyeColor, nationality);
            Coordinates coordinates = new Coordinates(x, y);
            Movie movie = new Movie(
                    id, name, oscarsCount, totalBoxOffice,
                    usaBoxOffice, coordinates, creationDate,
                    genre, operator
            );

            collection.add(movie);
        }
    }
}
