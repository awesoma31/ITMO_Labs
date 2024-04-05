package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.List;

public class Clear extends AbstractCommand{
    public Clear() {
        super("clear", "clears the collection");
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
