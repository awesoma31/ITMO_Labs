package org.awesoma.commands;

import org.awesoma.common.Response;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Coordinates;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.Person;
import org.awesoma.common.util.Asker;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This command updates an element with given id
 */
public class UpdateId extends AbstractClientCommand {
    public static final int argAmount = 1;



    public UpdateId(ObjectOutputStream out, ObjectInputStream in) {
        super("update_id", "This command updates an element with given id", in, out);

    }

    @Override
    public Response execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Asker asker = new Asker(reader);
            int idToFind = Integer.parseInt(args.get(0));

//            for (Movie curMovie : collection) {
//                if (idToFind == curMovie.getId()) {
//                    try {
//                        curMovie.setId(idToFind);
//                        curMovie.setName(asker.askName());
//                        curMovie.setCoordinates(new Coordinates(asker.askX(), asker.askY()));
//                        curMovie.setCreationDate(LocalDateTime.now());
//                        curMovie.setOscarsCount(asker.askOscarsCount());
//                        curMovie.setTotalBoxOffice(asker.askTotalBoxOffice());
//                        curMovie.setUsaBoxOffice(asker.askUsaBoxOffice());
//                        curMovie.setGenre(asker.askGenre());
//                        curMovie.setOperator(
//                                new Person(
//                                        asker.askOperatorName(),
//                                        asker.askBirthdate(),
//                                        asker.askWeight(),
//                                        asker.askEyeColor(),
//                                        asker.askNationality()
//                                )
//                        );
//                        return null;
//                    } catch (ValidationException e) {
//                        throw new CommandExecutingException(e.getMessage());
//                    }
//                }
//            }
            throw new CommandExecutingException("No movie with such id found"); // if movie with such id not found
        }
    }
}