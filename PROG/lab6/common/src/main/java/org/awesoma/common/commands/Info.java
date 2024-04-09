package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class Info extends AbstractCommand {
    public static final String name = "info";

    public Info() {
        super(Info.name, "shows some info about collection");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(this.name);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
