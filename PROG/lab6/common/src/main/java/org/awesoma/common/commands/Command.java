package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.List;

public interface Command {

    Request buildRequest(List<String> args);

//    Response execute(ArrayList<String> args);

//    default Response execute(ArrayList<String> args, Movie movie) {
//        return this.execute(args);
//    }

    default void handleResponse(Response response) {}
    String getName();

    String getDescription();

    Response accept(Visitor visitor, Request request);

    default String getHelp() {
        return "<" + this.getName() + ">: " + this.getDescription();
    }

    interface Visitor {
        Response visit(Help help);
        Response visit(Info info);
        Response visit(Show show);
        Response visit(Add add, Request request);
        Response visit(Clear clear);
    }
}
