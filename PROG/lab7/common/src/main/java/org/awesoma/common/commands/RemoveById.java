package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

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
    public Response accept(CommandVisitor visitor, Request request) {
        return visitor.visit(this, request);
    }
}
