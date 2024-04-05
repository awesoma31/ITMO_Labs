package org.awesoma.server.managers;

import org.awesoma.common.commands.*;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;
import org.awesoma.common.models.Movie;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.server.Server;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandInvoker implements Command.Visitor {
    private final CollectionManager collectionManager;
    private final DumpManager dumpManager;
    private final Server server;

    public CommandInvoker(CollectionManager collectionManager, Server server, DumpManager dumpManager) {
        this.collectionManager = collectionManager;
        this.server = server;
        collectionManager.updateIDs();
        this.dumpManager = dumpManager;
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
        collectionManager.update();
        Collections.sort(collectionManager.getCollection());

        return new Response(Status.OK);
    }

    @Override
    public Response visit(PrintFieldAscendingTBO printFieldAscendingTBO) {
        collectionManager.update();
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
        collectionManager.update();

        return new Response(Status.OK);
    }

    @Override
    public Response visit(RemoveById removeById, Request request) {
        var id = Integer.parseInt(request.getArgs().get(0));
        var col = collectionManager.getCollection();
        for (int i = 0; i < col.size(); i++) {
            if (col.get(i).getId() == id) {
                col.remove(i);
                return new Response(Status.OK);
            }
        }
        return new Response(Status.ERROR, "Item with such id not found");
    }

    @Override
    public Response visit(RemoveAt removeAt, Request request) {
        try {
            var index = Integer.parseInt(request.getArgs().get(0));
            collectionManager.getCollection().remove(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Response(Status.ERROR, "No item with such index");
        } catch (NumberFormatException e) {
            return new Response(Status.ERROR, "Index must be an integer");
        }
        return new Response(Status.OK);
    }

    @Override
    public Response visit(AddIfMax addIfMax, Request request) {
        var col = collectionManager.getCollection();
        var tbo = request.getMovie().getTotalBoxOffice();
        for (Movie m : col) {
            if (m.getTotalBoxOffice() > tbo) {
                return new Response(Status.WARNING, "Element wasn't added because its TBO is not maximum");
            }
        }
        col.add(request.getMovie());
        collectionManager.update();
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
    public Response visit(Save save) {
        collectionManager.update();
        collectionManager.getCollection().sort(Movie::compareTo);
        try {
            dumpManager.writeCollection(collectionManager.getCollection());
            // todo error
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return new Response(Status.ERROR, e.getMessage());
        }
        return new Response(Status.OK);
    }

    @Override
    public Response visit(Add add, Request request) {
        collectionManager.getCollection().add(request.getMovie());
        //todo
        collectionManager.update();
        return new Response(Status.OK);
    }
}
