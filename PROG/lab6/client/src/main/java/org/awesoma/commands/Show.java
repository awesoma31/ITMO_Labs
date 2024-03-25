package org.awesoma.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.exceptions.WrongAmountOfArgumentsException;
import org.awesoma.common.models.Movie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This commands shows elements of the collection
 */
public class Show extends AbstractClientCommand {


    public Show(ObjectOutputStream out, ObjectInputStream in) {
        super(
                "show",
                "This commands shows elements of the collection",
                in,
                out
        );
    }

    @Override
    public Response execute(ArrayList<String> args) throws IOException {
        serverWriter.writeObject(new Request(this.name, null, args));
        serverWriter.flush();
        try {
            Response response = (Response) serverReader.readObject();
            @SuppressWarnings("unchecked")
            Vector<Movie> col = (Vector<Movie>) response.getExtraData();
            System.out.println("stored data: " + col);
            return response;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
