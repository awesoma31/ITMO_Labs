package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class RemoveAtCommand extends Command {
    public static final String NAME = "remove_at";

    public RemoveAtCommand() {
        super(NAME, "removes element by index");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, args);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        this.userCredentials = request.getUserCredentials();
        return visitor.visit(this, request);
    }
}
