package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.util.Asker;

import java.util.ArrayList;

public class AddIfMax extends AbstractCommand implements Ask {
    public static final String NAME = "add_if_max";

    public AddIfMax() {
        super(NAME, "adds an element if its total box office is maximum");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, askMovie(new Asker(reader)));
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this, request);
    }
}
