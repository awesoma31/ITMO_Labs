package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;

public interface Command {
    String getName();
    String getDescription();
    default String getHelp() {
        return "<" + this.getName() + ">: " + this.getDescription();
    }
    Request buildRequest();
    Response execute(ArrayList<String> args);
    default Response execute(ArrayList<String> args, Movie movie) {
        return this.execute(args);
    }
    void handleResponse(Response response);
}
