package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class SortCommand extends Command {
    public static String name = "sort";

    public SortCommand() {
        super(SortCommand.name, "sorts the collection by ID");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this);
    }

    @Override
    public boolean isShowInHelp() {
        return false;
    }
}
