package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class ExitCommand extends Command {
    public static final String NAME = "exit";

    public ExitCommand() {
        super(ExitCommand.NAME, "stops the program");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        System.out.println("Exiting");
        System.exit(0);
        return null;
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        System.err.println("Exiting");
        System.exit(0);
        return  null;
//        return visitor.visit(this);
    }

    @Override
    public void handleResponse(Response response) {
        System.out.println("Exiting");
        System.exit(0);
    }
}
