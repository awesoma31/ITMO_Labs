package org.awesoma.server.commands;

import org.awesoma.common.models.Movie;

import java.util.ArrayList;

public class ShowCommand extends AbstractServerCommand{
    public ShowCommand() {
        super("show");
    }

    @Override
    public void execute(ArrayList<String> args, Movie movie) {

    }
}
