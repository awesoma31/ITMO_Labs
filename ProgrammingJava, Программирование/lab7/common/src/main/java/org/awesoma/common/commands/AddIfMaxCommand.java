package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Ask;
import org.awesoma.common.util.Asker;

import java.util.ArrayList;

public class AddIfMaxCommand extends Command implements Ask {
    public static final String NAME = "add_if_max";

    public AddIfMaxCommand() {
        super(NAME, "adds an element if its total box office is maximum");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, askMovie(new Asker(reader)));
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        this.userCredentials = request.getUserCredentials();
        return visitor.visit(this, request);
    }
}
