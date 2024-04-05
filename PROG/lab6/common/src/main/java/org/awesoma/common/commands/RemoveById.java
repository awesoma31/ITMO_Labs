package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;

import java.util.ArrayList;

public class RemoveById extends AbstractCommand {
    public static final String NAME = "remove_by_id";

    public RemoveById() {
        super(NAME, "remove element from collection by id");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(NAME, args);
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this, request);
    }

    @Override
    public void handleResponse(Response response) {
        if (response.getStatusCode() == Status.ERROR) {
            System.err.println("[ERROR]: " + response.getMessage());
        }
    }
}
