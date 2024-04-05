package org.awesoma.common.commands;

import org.awesoma.common.Environment;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Show extends AbstractCommand {
    public Show() {
        super("show", "shows stored data");
    }

    @Override
    public Request buildRequest(List<String> args) {
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
