package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.List;

public class Sort extends AbstractCommand{
    public Sort() {
        super("sort", "sorts the collection by ID");
    }

    @Override
    public Request buildRequest(List<String> args) {
        return new Request(name);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
