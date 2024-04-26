package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.commands.*;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CommandInvoker implements CommandVisitor {
    private final Logger logger = LogManager.getLogger(CommandInvoker.class);
    private final CollectionManager collectionManager;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final DBManager db;

    public CommandInvoker(CollectionManager collectionManager, DBManager db) {
        this.collectionManager = collectionManager;
        this.db = db;
//        collectionManager.update();
    }

    private Response invoke(InvocationType invocationType, InvocationLogic logic) {
        updateCollectionFromDB();
        if (invocationType == InvocationType.WRITE) {
            try {
                if (readLock.tryLock(1L, TimeUnit.MINUTES)) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) { // GOVNOCODE
                logger.error(e);
                return new Response(Status.ERROR, e.getMessage());
            } finally {
                readLock.unlock();
                updateCollectionFromDB();
            }
        } else if (invocationType == InvocationType.READ) {
            try {
                if (writeLock.tryLock(1L, TimeUnit.MINUTES)) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) { // GOVNOCODE
                return new Response(Status.ERROR, e.getMessage());
            } finally {
                writeLock.unlock();
                updateCollectionFromDB();
            }
        } else if (invocationType == InvocationType.READ_WRITE) {
            try {
                if (writeLock.tryLock() && readLock.tryLock()) {
                    return logic.execute();
                } else {
                    return new Response(Status.ERROR, "Command wasn't executed due to lock unavailability");
                }
            } catch (Exception e) { // GOVNOCODE
                return new Response(Status.ERROR, e.getMessage());
            } finally {
                writeLock.unlock();
                readLock.unlock();
                updateCollectionFromDB();
            }
        }
        throw new CommandExecutingException("invoke fail");
    }

    public Response visit(ClearCommand clear) {
        return invoke(InvocationType.WRITE, () -> {
            try {
                db.clear(clear.getUserCredentials().username());
            } catch (SQLException e) {
                throw new CommandExecutingException("" + e);
            }
            collectionManager.clearCollection();
            return new Response(Status.OK);
        });
    }

    @Override
    public Response visit(PrintFieldAscendingTBOCommand printFieldAscendingTBO) {
        return invoke(InvocationType.READ, () -> {
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
    public Response visit(UpdateIdCommand updateId, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            var id = Integer.parseInt(request.getArgs().get(0));
            try {
                db.updateElementById(id, request.getMovie(), request.getUserCredentials());
            } catch (CommandExecutingException e) {
                return new Response(Status.ERROR, e.getMessage());
            }
            return new Response(Status.OK);
        });
    }

    @Override
    public Response visit(RemoveByIdCommand removeById, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            var id = Integer.parseInt(request.getArgs().get(0));

            try {
                db.removeById(id, request.getUserCredentials());
                return new Response(Status.OK);
            } catch (CommandExecutingException e) {
                return new Response(Status.ERROR, e.getMessage());
            }
        });
    }

    @Override
    public Response visit(RemoveAtCommand removeAt, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            try {
                var index = Integer.parseInt(request.getArgs().get(0));
                var col = collectionManager.getCollection();
                var id = col.get(index).getId();
                db.removeById(id, request.getUserCredentials());
                return new Response(Status.OK);
            } catch (ArrayIndexOutOfBoundsException e) {
                return new Response(Status.ERROR, "No item with such index");
            } catch (NumberFormatException e) {
                return new Response(Status.ERROR, "Index must be an integer");
            }
        });
    }

    @Override
    public Response visit(AddIfMaxCommand addIfMax, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            var col = collectionManager.getCollection();
            var tbo = request.getMovie().getTotalBoxOffice();

            boolean isMaxTbo = col.stream()
                    .mapToInt(Movie::getTotalBoxOffice)
                    .noneMatch(existingTbo -> existingTbo > tbo);

            if (!isMaxTbo) {
                return new Response(Status.WARNING, "Element wasn't added because its TBO is not maximum");
            }
            try {
                db.addMovie(request.getMovie(), request.getUserCredentials());
            } catch (SQLException e) {
                throw new CommandExecutingException(e.getMessage());
            }
            return new Response(Status.OK, "Movie was added successfully");
        });
    }

    @Override
    public Response visit(InfoCommand info) {
        return invoke(InvocationType.READ, () -> {
            String data;
            data = "Collection type: " + collectionManager.getCollection().getClass() +
                    "\nCollection size: " + collectionManager.getCollection().size() +
                    "\nCollection initialization date: " + collectionManager.getInitDate();
            return new Response(Status.OK, data);
        });
    }

    private void updateCollectionFromDB() {
        try {
            collectionManager.setCollection(db.readCollection());
        } catch (SQLException e) {
            throw new CommandExecutingException(e.getMessage());
        }
    }

    @Override
    public Response visit(ShowCommand show) {
        return invoke(InvocationType.READ, () -> {
            String data;
            data = "[STORED DATA]:\n" + collectionManager.getCollection().stream()
                    .map(Movie::toString)
                    .collect(Collectors.joining("\n"));
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
        // todo change to stop()
        System.exit(1);
        return null;
    }

    @Override
    public Response visit(AddCommand add, Request request) {
        return invoke(InvocationType.WRITE, () -> {
            try {
                db.addMovie(request.getMovie(), request.getUserCredentials());
                return new Response(Status.OK, "Movie added successfully");
            } catch (SQLException e) {
                throw new CommandExecutingException(e.getMessage());
            }
        });
    }

    private enum InvocationType {
        READ, WRITE, READ_WRITE
    }

    @FunctionalInterface
    private interface InvocationLogic {
        Response execute();
    }
}
