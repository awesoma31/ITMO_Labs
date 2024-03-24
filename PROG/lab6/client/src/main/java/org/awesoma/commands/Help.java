package org.awesoma.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.exceptions.CommandExecutingException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Help extends AbstractClientCommand{
    public Help(ObjectInputStream serverReader, ObjectOutputStream serverWriter) {
        super("help", "shows info about commands", serverReader, serverWriter);
    }

    @Override
    public Response execute(ArrayList<String> args) throws CommandExecutingException, IOException {
        serverWriter.writeObject(new Request(this.name, null, args));
        serverWriter.flush();
        try {
            return (Response) serverReader.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
