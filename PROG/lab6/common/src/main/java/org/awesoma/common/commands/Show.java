package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;

import java.util.ArrayList;

public class Show extends AbstractCommand {
    public static String name = "show";

    public Show() {
        super(Show.name, "shows stored data");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        return new Request(getName());
    }

//    @Override
//    public Response execute(ArrayList<String> args) {
//        // todo надо бы перенести
//        String data = "[STORED DATA]:\n" + Environment.collection.stream()
//                .map(Movie::toString)
//                .collect(Collectors.joining("\n"));
//
//        return new Response(Status.OK, data, data);
//    }

    @Override
    public void handleResponse(Response response) {
        if (response.getStatusCode() == Status.OK) {
            System.out.println(response.getMessage());
        } else {
            System.err.println(response.getStatusCode() + " " + response.getMessage());
        }
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
