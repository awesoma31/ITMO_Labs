package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class Save extends AbstractCommand {
    public static final String NAME = "save";

    public Save() {
        super(NAME, "saves collection to file");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        // todo delete from client
        System.err.println("TODO: DELETE THIS COMMAND FROM CLIENT!!!!!!!!!!!!!!!!!!!");
        return new Request(NAME);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
