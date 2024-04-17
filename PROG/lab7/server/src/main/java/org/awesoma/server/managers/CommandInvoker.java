package org.awesoma.server.managers;

import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;
import org.awesoma.server.TCPServer;
import org.awesoma.server.util.json.DumpManager;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CommandInvoker implements CommandVisitor {
    private final CollectionManager collectionManager;
    private final DumpManager dumpManager;
    private final TCPServer server;
    private final ReentrantReadWriteLock.ReadLock readLock = new ReentrantReadWriteLock().readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();

    public CommandInvoker(TCPServer server) {
        this.server = server;
        collectionManager = server.getCollectionManager();
        collectionManager.update();
        dumpManager = server.getDumpManager();
    }

    private Response invoke(InvocationType invocationType, InvocationLogic logic) {
        if (invocationType == InvocationType.WRITE) {
            try {
                if (writeLock.tryLock(1L, TimeUnit.MINUTES)) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) {
                return new Response(Status.ERROR);
            } finally {
                writeLock.unlock();
            }
        } else if (invocationType == InvocationType.READ){
            try {
                if (readLock.tryLock(1L, TimeUnit.MINUTES)) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) {
                return new Response(Status.ERROR);
            } finally {
                readLock.unlock();
            }
        } else if (invocationType == InvocationType.READ_WRITE) {
            try {
                if (writeLock.tryLock() && readLock.tryLock()) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) {
                return new Response(Status.ERROR);
            } finally {
                writeLock.unlock();
                readLock.unlock();
            }
        }
        throw new RuntimeException("invoke fail");
    }

    public Response visit(ClearCommand clear) {
        return invoke(InvocationType.WRITE, () -> {
            collectionManager.clearCollection();
            return new Response(Status.OK);
        });
    }

    @Override
    public Response visit(PrintFieldAscendingTBOCommand printFieldAscendingTBO) {
        return invoke(InvocationType.READ, () -> {
            collectionManager.update();
            String data = "[TBO ascended]:\n" + collectionManager.getCollection().stream()
                    .sorted(Comparator.comparingInt(Movie::getTotalBoxOffice))
                    .map(movie -> String.valueOf(movie.getTotalBoxOffice()))
                    .collect(Collectors.joining(", "));

            return new Response(Status.OK, data);
        });
    }

    @Override
    public Response visit(SortCommand sort) {
        return invoke(InvocationType.READ, () -> {
           collectionManager.sortCollection();
            return new Response(Status.OK);
        });
    }

    @Override
    public synchronized Response visit(UpdateIdCommand updateId, Request request) {
        return invoke(InvocationType.WRITE, () -> {
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
        });
    }

    @Override
    public synchronized Response visit(RemoveByIdCommand removeById, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            var id = Integer.parseInt(request.getArgs().get(0));
            var col = collectionManager.getCollection();

            if (col.removeIf(movie -> movie.getId() == id)) {
                return new Response(Status.OK);
            } else {
                return new Response(Status.ERROR, "Item with such id not found");
            }
        });
    }

    @Override
    public synchronized Response visit(RemoveAtCommand removeAt, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            try {
                var index = Integer.parseInt(request.getArgs().get(0));
                collectionManager.removeByIndex(index);
                return new Response(Status.OK);
            } catch (ArrayIndexOutOfBoundsException e) {
                return new Response(Status.ERROR, "No item with such index");
            } catch (NumberFormatException e) {
                return new Response(Status.ERROR, "Index must be an integer");
            }
        });
    }

    @Override
    public synchronized Response visit(AddIfMaxCommand addIfMax, Request request) {
        return invoke(InvocationType.WRITE, () -> {var col = collectionManager.getCollection();
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

        });
    }

    @Override
    public Response visit(InfoCommand info) {
        return invoke(InvocationType.READ, () -> {
            String data;
            synchronized (this) {

                data = "Collection type: " + collectionManager.getCollection().getClass() +
                        "\nCollection size: " + collectionManager.getCollection().size() +
                        "\nCollection initialization date: " + collectionManager.getInitDate();
            }
            return new Response(Status.OK, data);
        });
    }

    @Override
    public Response visit(ShowCommand show) {
        return invoke(InvocationType.READ, () -> {
            String data;
            synchronized (this) {
                data = "[STORED DATA]:\n" + collectionManager.getCollection().stream()
                        .map(Movie::toString)
                        .collect(Collectors.joining("\n"));
            }
            return new Response(Status.OK, data);
        });
    }

    @Override
    public Response visit(HelpCommand help) {
        return invoke(InvocationType.READ, () -> {
            String data = "[AVAILABLE COMMANDS]:\n" + Environment.getAvailableCommands().values().stream()
                    .map(Command::getHelp)
                    .collect(Collectors.joining("\n"));
            return new Response(Status.OK, data);
        });
    }

    @Override
    public Response visit(ExitCommand exit) {
        // ALERT!!! GOVNOCODE
        try {
            saveCollection();
        } catch (IOException e) {
            server.closeConnection();
            return new Response(Status.ERROR, "Collection wasn't saved");
        }
        return new Response(Status.ERROR, "connection wasn't closed");
    }

    @Override
    public synchronized Response visit(SaveCommand save) {
        return invoke(InvocationType.WRITE, () -> {
            try {
                saveCollection();
            } catch (IOException e) {
                return new Response(Status.ERROR, e.getMessage());
            }
            return new Response(Status.OK);
        });
    }

    @Override
    public Response visit(AddCommand add, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            collectionManager.addMovie(request.getMovie());
            return new Response(Status.OK, "Movie added successfully");
        });
    }

    private void saveCollection() throws IOException {
        collectionManager.sortCollection();
        dumpManager.writeCollection(collectionManager.getCollection());
    }

    @FunctionalInterface
    private interface InvocationLogic {
        Response execute();
    }
    private enum InvocationType {
        READ, WRITE, READ_WRITE
    }
}
