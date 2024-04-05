package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.List;

public class Info extends AbstractCommand{
    public static final String name = "info";
    public Info() {
        super(Info.name, "shows some info about collection");
    }

    @Override
    public Request buildRequest(List<String> args) {
        return new Request(this.name);
    }

    @Override
    public void handleResponse(Response response) {
        System.out.println(response.getMessage());
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
