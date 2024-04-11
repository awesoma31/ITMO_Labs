package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class Show extends AbstractCommand {
    public static String NAME = "show";

    public Show() {
        super(NAME, "shows stored data");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(getName());
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
