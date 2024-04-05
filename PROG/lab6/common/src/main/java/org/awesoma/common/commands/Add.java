package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.util.Asker;

import java.util.List;

public class Add extends AbstractCommand implements Ask{
    public static String name = "add";

    public Add() {
        super(Add.name, "adds an element to the collection");
    }

    @Override
    public Request buildRequest(List<String> args) {
        return new Request(this.name, askMovie(new Asker(reader)), null);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        // todo args give
        return visitor.visit(this, request);
    }
}
