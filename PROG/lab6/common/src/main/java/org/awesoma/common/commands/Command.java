package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.ArrayList;

public interface Command {
    Request buildRequest(ArrayList<String> args);

    default void handleResponse(Response response) {
        switch (response.getStatusCode()) {
            case ERROR:
                System.err.println("[ERROR]: " + response.getMessage());
                break;
            case WARNING:
                System.out.println("[WARNING]: " + response.getMessage());
                break;
        }
    }

    Response accept(Visitor visitor, Request request);

    String getName();

    String getDescription();

    default String getHelp() {
        return "<" + this.getName() + ">: " + this.getDescription();
    }

    interface Visitor {
        Response visit(Help help);

        Response visit(Info info);

        Response visit(Show show);

        Response visit(Add add, Request request);

        Response visit(Clear clear);

        Response visit(Sort sort);

        Response visit(PrintFieldAscendingTBO printFieldAscendingTBO);

        Response visit(UpdateId updateId, Request request);

        Response visit(RemoveById removeById, Request request);

        Response visit(RemoveAt removeAt, Request request);

        Response visit(AddIfMax addIfMax, Request request);

        Response visit(Save save);
    }
}
