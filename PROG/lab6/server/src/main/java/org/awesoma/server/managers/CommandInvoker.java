package org.awesoma.server.managers;

import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;
import org.awesoma.server.util.json.DumpManager;
import org.awesoma.server.TCPServer;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CommandInvoker implements CommandVisitor {
    private final CollectionManager collectionManager;
    private final DumpManager dumpManager;
    private final TCPServer server;


    public CommandInvoker(TCPServer server) {
        this.server = server;
        this.collectionManager = server.getCollectionManager();
        collectionManager.updateIDs();
        this.dumpManager = server.getDumpManager();
    }

    @Override
    public Response visit(ClearCommand clear) {
        collectionManager.clearCollection();
        return new Response(Status.OK);
    }

    @Override
    public Response visit(SortCommand sort) {
        collectionManager.sortCollection();
        return new Response(Status.OK);
    }

    @Override
    public Response visit(PrintFieldAscendingTBOCommand printFieldAscendingTBO) {
        collectionManager.update();
        String data = "TBO ascended:\n" + collectionManager.getCollection().stream()
                .sorted(Comparator.comparingInt(Movie::getTotalBoxOffice))
                .map(movie -> String.valueOf(movie.getTotalBoxOffice()))
                .collect(Collectors.joining(", "));

        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(UpdateIdCommand updateId, Request request) {
        var id = Integer.parseInt(request.getArgs().get(0));
        var col = collectionManager.getCollection();

        col.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .ifPresent(movie -> {
                    int index = col.indexOf(movie);
                    col.set(index, request.getMovie());
                    col.get(index).setId(id);
                });

        collectionManager.update();
        return new Response(Status.OK);
    }

    @Override
    public Response visit(RemoveByIdCommand removeById, Request request) {
        var id = Integer.parseInt(request.getArgs().get(0));
        var col = collectionManager.getCollection();

        if (col.removeIf(movie -> movie.getId() == id)) {
            return new Response(Status.OK);
        } else {
            return new Response(Status.ERROR, "Item with such id not found");
        }
    }

    @Override
    public Response visit(RemoveAtCommand removeAt, Request request) {
        try {
            var index = Integer.parseInt(request.getArgs().get(0));
            collectionManager.removeByIndex(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Response(Status.ERROR, "No item with such index");
        } catch (NumberFormatException e) {
            return new Response(Status.ERROR, "Index must be an integer");
        }
        return new Response(Status.OK);
    }

    @Override
    public Response visit(AddIfMaxCommand addIfMax, Request request) {
        var col = collectionManager.getCollection();
        var tbo = request.getMovie().getTotalBoxOffice();

        boolean isMaxTbo = col.stream()
                .mapToInt(Movie::getTotalBoxOffice)
                .noneMatch(existingTbo -> existingTbo > tbo);

        if (!isMaxTbo) {
            return new Response(Status.WARNING, "Element wasn't added because its TBO is not maximum");
        }

        col.add(request.getMovie());
        collectionManager.update();

        return new Response(Status.OK);
    }

    @Override
    public Response visit(InfoCommand info) {
        String data = "Collection type: " + collectionManager.getCollection().getClass() +
                "\nCollection size: " + collectionManager.getCollection().size() +
                "\nCollection initialization date: " + collectionManager.getInitDate();
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(ShowCommand show) {
        String data = "[STORED DATA]:\n" + collectionManager.getCollection().stream()
                .map(Movie::toString)
                .collect(Collectors.joining("\n"));
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(HelpCommand help) {
        String data = "[AVAILABLE COMMANDS]:\n" + Environment.getAvailableCommands().values().stream()
                .map(Command::getHelp)
                .collect(Collectors.joining("\n"));
        return new Response(Status.OK, data);
    }

    @Override
    public Response visit(ExitCommand exit) {
        // govnocode starts here
        try {
            saveCollection();
        } catch (IOException e) {
            server.closeConnection();
            return new Response(Status.ERROR, "Collection wasn't saved");
        }
        return new Response(Status.OK);
    }

    @Override
    public Response visit(SaveCommand save) {
        try {
            saveCollection();
        } catch (IOException e) {
            return new Response(Status.ERROR, e.getMessage());
        }
        return new Response(Status.OK);
    }

    @Override
    public Response visit(AddCommand add, Request request) {
        collectionManager.addMovie(request.getMovie());
        return new Response(Status.OK, "Movie added successfully");
    }

    private void saveCollection() throws IOException {
        collectionManager.sortCollection();
        dumpManager.writeCollection(collectionManager.getCollection());
    }
}
