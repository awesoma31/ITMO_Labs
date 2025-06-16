package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Asker;

import java.util.ArrayList;

import static org.awesoma.common.util.Asker.askMovie;

public class AddCommand extends Command {
    public static String NAME = "add";

    public AddCommand() {
        super(AddCommand.NAME, "adds an element to the collection");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, askMovie(new Asker(reader)));
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        // todo args give
        this.userCredentials = request.getUserCredentials();
        return visitor.visit(this, request);
    }
}
