package awesoma.common.exceptions.commands;

import awesoma.common.exceptions.Asker;
import awesoma.common.exceptions.exceptions.CommandExecutingException;
import awesoma.common.exceptions.exceptions.ValidationException;
import awesoma.common.exceptions.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.exceptions.models.Coordinates;
import awesoma.common.exceptions.models.Movie;
import awesoma.common.exceptions.models.Person;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This command updates an element with given id
 */
public class UpdateId extends Command {
    public static final int argAmount = 1;
    private final Vector<Movie> collection;


    public UpdateId(Vector<Movie> collection, BufferedReader reader) {
        super("update_id", "This command updates an element with given id");
        this.collection = collection;
        this.reader = reader;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Asker asker = new Asker(reader);
            int idToFind = Integer.parseInt(args.get(0));

            for (Movie curMovie : collection) {
                if (idToFind == curMovie.getId()) {
                    try {
                        curMovie.setId(idToFind);
                        curMovie.setName(asker.askName());
                        curMovie.setCoordinates(new Coordinates(asker.askX(), asker.askY()));
                        curMovie.setCreationDate(LocalDateTime.now());
                        curMovie.setOscarsCount(asker.askOscarsCount());
                        curMovie.setTotalBoxOffice(asker.askTotalBoxOffice());
                        curMovie.setUsaBoxOffice(asker.askUsaBoxOffice());
                        curMovie.setGenre(asker.askGenre());
                        curMovie.setOperator(
                                new Person(
                                        asker.askOperatorName(),
                                        asker.askBirthdate(),
                                        asker.askWeight(),
                                        asker.askEyeColor(),
                                        asker.askNationality()
                                )
                        );
                        return;
                    } catch (ValidationException e) {
                        throw new CommandExecutingException(e.getMessage());
                    }
                }
            }
            throw new CommandExecutingException("No movie with such id found"); // if movie with such id not found
        }
    }
}
