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
import java.util.Vector;
import java.util.stream.Collectors;

public class CommandInvoker implements Command.Visitor {
    private final CollectionManager collectionManager;
    private final Server server;

    public CommandInvoker(CollectionManager collectionManager, Server server) {
        this.collectionManager = collectionManager;
        this.server = server;
        collectionManager.updateIDs();
    }

    @Override
    public Response visit(Help help) {

        return new Response(Status.OK);
    }

    @Override
    public Response visit(Clear clear) {
        collectionManager.getCollection().clear();
        return new Response(Status.OK);
    }

    @Override
    public Response visit(Sort sort) {
        collectionManager.updateIDs();
        Collections.sort(collectionManager.getCollection());

        return new Response(Status.OK);
    }

    @Override
    public Response visit(PrintFieldAscendingTBO printFieldAscendingTBO) {
        collectionManager.updateIDs();
        String data = "TBO ascended:\n" + collectionManager.getCollection().stream()
                .sorted(Comparator.comparingInt(Movie::getTotalBoxOffice))
                .map(movie -> String.valueOf(movie.getTotalBoxOffice()))
                .collect(Collectors.joining(", "));
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(UpdateId updateId, Request request) {
        var id = Integer.parseInt(request.getArgs().get(0));
        var col = collectionManager.getCollection();

        for (int i = 0; i < col.size(); i++) {
            if (col.get(i).getId() == id) {
                col.set(i, request.getMovie());
                col.get(i).setId(id);
            }
        }
        collectionManager.updateIDs();

        return new Response(Status.OK);
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
        //todo
        collectionManager.updateIDs();
        return new Response(Status.OK);
    }


}
