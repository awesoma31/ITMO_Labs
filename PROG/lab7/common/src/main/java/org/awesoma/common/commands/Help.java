package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class Help extends AbstractCommand {
    public static String name = "help";

    public Help() {
        super(Help.name, "shows available commands");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(this.getName());
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }
}
