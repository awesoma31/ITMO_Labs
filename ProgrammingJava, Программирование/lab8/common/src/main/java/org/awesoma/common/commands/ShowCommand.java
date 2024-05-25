package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class ShowCommand extends Command {
    public static String NAME = "show";

    public ShowCommand() {
        super(NAME, "shows stored data");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(getName());
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }

    @Override
    public boolean isShownInHelp() {
        return false;
    }
}
