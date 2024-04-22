package awesoma.common.exceptions.commands;

import awesoma.common.exceptions.Asker;
import awesoma.common.exceptions.UniqueIdGenerator;
import awesoma.common.exceptions.exceptions.CommandExecutingException;
import awesoma.common.exceptions.exceptions.ValidationException;
import awesoma.common.exceptions.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.exceptions.models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

/**
 * Adds an element in the collection if its totalBoxOffice value is maximum in the collection
 */
public class AddIfMax extends Command {
    private final Vector<Movie> collection;
    private final UniqueIdGenerator idGenerator;
    private final HashSet<Integer> idList;

    public AddIfMax(UniqueIdGenerator idGenerator, Vector<Movie> collection) {
        super(
                "add_if_max",
                "This command adds an element to the collection if its totalBoxOffice " +
                        "is the biggest in the collection"
        );
        this.idList = idGenerator.getIdList();
        this.idGenerator = idGenerator;
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Integer maxTotalBoxOffice = 0;
            Asker asker = new Asker(reader);

            // set max TBO
            for (Movie m : collection) {
                if (m.getTotalBoxOffice() > maxTotalBoxOffice) {
                    maxTotalBoxOffice = m.getTotalBoxOffice();
                }
            }

            int totalBoxOffice = asker.askTotalBoxOffice();

            if (totalBoxOffice < maxTotalBoxOffice) {
                System.out.println(
                        "[ABORTION]: inputted totalBoxOffice is not the biggest in the collection, " +
                                "element won't be added anyway"
                );
                return;
            }

            int id = idGenerator.generateUniqueId();
            String name = asker.askName();
            double x = asker.askX();
            Long y = asker.askY();
            LocalDateTime creationDate = LocalDateTime.now();
            Integer oscarsCount = asker.askOscarsCount();
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

    public HashSet<Integer> getIdList() {
        return idList;
    }
}
