package awesoma.common.commands;

import awesoma.common.exceptions.ArgParsingException;
import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.*;
import awesoma.common.util.Asker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

            for (int i = 0; i < collection.size(); i++) {
                Movie curMovie = collection.get(i);
                if (idToFind == collection.get(i).getId()) {
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
                }
            }
        }
    }
}
