package org.awesoma.server.util;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;
import org.awesoma.server.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class CommandInvoker {
    private final HashMap<String, Command> commands = new HashMap<>();
    private Vector<Movie> collection;
    private final ShowCommand showCommand;
    private final AddCommand addCommand;
    private final AddIfMaxCommand addIfMaxCommand;

    public CommandInvoker(Vector<Movie> collection, ShowCommand showCommand, AddCommand addCommand, AddIfMaxCommand addIfMaxCommand) {
        this.collection = collection;

        this.showCommand = showCommand;
        this.addCommand = addCommand;
        this.addIfMaxCommand = addIfMaxCommand;

        commands.put("show", showCommand);
        commands.put("add", addCommand);

//        this.addCommand.setCollection(collection);
    }

    public Response invoke(Request request) {
        try {
            return commands.get(request.getCommandName())
                    .execute(request.getArgs(), request.getMovie());
        } catch (NullPointerException e) {
            return new Response(StatusCode.ERROR, request.getCommandName() + " not found");
        } catch (CommandExecutingException e) {
            return new Response(StatusCode.ERROR, request.getCommandName() + " command execution failed");
        }
    }

    public void setCollection(Vector<Movie> collection) {
        this.collection = collection;
    }
}
