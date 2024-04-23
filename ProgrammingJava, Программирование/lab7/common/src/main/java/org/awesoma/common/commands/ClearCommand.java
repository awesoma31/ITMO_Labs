package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class ClearCommand extends Command {
    public static String name = "clear";

    public ClearCommand() {
        super(ClearCommand.name, "clears the collection");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        this.userCredentials = request.getUserCredentials();
        return visitor.visit(this);
    }
}
