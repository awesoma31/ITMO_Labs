package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class InfoCommand extends Command {
    public static final String NAME = "info";

    public InfoCommand() {
        super(InfoCommand.NAME, "shows some info about collection");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }
}
