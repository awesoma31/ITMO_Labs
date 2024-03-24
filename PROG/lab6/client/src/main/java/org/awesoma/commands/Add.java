package org.awesoma.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.Asker;
import org.awesoma.common.util.UniqueIdGenerator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This command adds an element with given fields to the collection
 */
public class Add extends AbstractClientCommand {
    private Vector<Movie> collection;
    private UniqueIdGenerator idGenerator;
    private Asker asker;


    public Add(ObjectOutputStream out, ObjectInputStream in) {
        super("add", "this command adds an element to the collection", in,
                out);
    }

    @Override
    public Response execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount & this.reader != null) {
            throw new WrongAmountOfArgumentsException();
        } else {
            asker = new Asker(reader);
            try {
                Movie movie = new Movie(
                        asker.askName(),
                        asker.askOscarsCount(),
                        asker.askTotalBoxOffice(),
                        asker.askUsaBoxOffice(),
                        new Coordinates(asker.askX(), asker.askY()),
                        asker.askGenre(),
                        new Person(
                                asker.askOperatorName(),
                                asker.askBirthdate(),
                                asker.askWeight(),
                                asker.askEyeColor(),
                                asker.askNationality()
                        )
                );

                return sendRequest(new Request(this.name, movie, args));
            } catch (ValidationException e) {
                throw new CommandExecutingException(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
