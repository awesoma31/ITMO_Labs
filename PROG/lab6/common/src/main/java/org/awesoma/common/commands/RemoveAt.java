package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class RemoveAt extends AbstractCommand {
    public static final String NAME = "remove_at";

    public RemoveAt() {
        super(NAME, "removes element by index");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, args);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this, request);
    }
}