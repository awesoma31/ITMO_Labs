package org.awesoma.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.Asker;
import org.awesoma.common.util.UniqueIdGenerator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

/**
 * Adds an element in the collection if its totalBoxOffice value is maximum in the collection
 */
public class AddIfMax extends AbstractClientCommand {
    private Vector<Movie> collection;
    private UniqueIdGenerator idGenerator;
    private HashSet<Integer> idList;
    private Asker asker;

    public AddIfMax(ObjectOutputStream out, ObjectInputStream in) {
        super(
                "add_if_max",
                "This command adds an element to the collection if its totalBoxOffice " +
                        "is the biggest in the collection", in, out

        );

    }

    @Override
    public Response execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) throw new WrongAmountOfArgumentsException();
        else {
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
                return new Response(StatusCode.OK, "", null);
            } catch (ValidationException e) {
                throw new CommandExecutingException(e.getMessage());
            }
        }
    }

    public HashSet<Integer> getIdList() {
        return idList;
    }
}
