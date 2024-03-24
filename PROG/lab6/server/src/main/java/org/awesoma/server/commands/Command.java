package org.awesoma.server.commands;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;

public interface Command {
    void execute(ArrayList<String> args, Movie movie) throws CommandExecutingException;
}
