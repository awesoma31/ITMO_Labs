package org.awesoma.server.managers;

import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;
import org.awesoma.common.models.Movie;
import org.awesoma.server.Server;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandInvoker implements Command.Visitor {
    private final CollectionManager collectionManager;
    private final Server server;

    public CommandInvoker(CollectionManager collectionManager, Server server) {
        this.collectionManager = collectionManager;
        this.server = server;
    }

    @Override
    public Response visit(Help help) {
//        String data = "";
        return new Response(Status.OK);
    }

    @Override
    public Response visit(Clear clear) {
        collectionManager.getCollection().clear();
        return new Response(Status.OK);
    }

    @Override
    public Response visit(Sort sort) {
        Collections.sort(collectionManager.getCollection());
        return new Response(Status.OK);
    }

    @Override
    public Response visit(PrintFieldAscendingTBO printFieldAscendingTBO) {
        String data = "TBO ascended:\n" + collectionManager.getCollection().stream()
                .sorted(Comparator.comparingInt(Movie::getTotalBoxOffice))
                .map(movie -> String.valueOf(movie.getTotalBoxOffice()))
                .collect(Collectors.joining(", "));
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(Info info) {
        String data = "Collection size: " + collectionManager.getCollection().size() +
                "\nCollection initialization date: " +
                collectionManager.getInitDate();
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(Show show) {
        String data = "[STORED DATA]:\n" + collectionManager.getCollection().stream()
                .map(Movie::toString)
                .collect(Collectors.joining("\n"));
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(Add add, Request request) {
        collectionManager.getCollection().add(request.getMovie());
        return new Response(Status.OK);
    }


}
