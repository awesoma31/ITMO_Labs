package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

// todo move to client
public class Exit extends AbstractCommand {
    public static final String NAME = "exit";

    public Exit() {
        super(Exit.NAME, "stops the program");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        System.out.println("Exiting");
        System.exit(0);
        return null;
//        return new Request(this.name);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }

    @Override
    public void handleResponse(Response response) {
        System.out.println("Exiting");
        System.exit(0);
    }
}
