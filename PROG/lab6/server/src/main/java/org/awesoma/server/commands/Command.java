package org.awesoma.server.commands;

import org.awesoma.common.Response;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;

public interface Command {
    Response execute(ArrayList<String> args, Movie movie) throws CommandExecutingException;
}
