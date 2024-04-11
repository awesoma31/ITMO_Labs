package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;
import org.awesoma.common.network.Status;

import java.util.ArrayList;
import java.util.Objects;

public interface Command {
    Request buildRequest(ArrayList<String> args);

    default void handleResponse(Response response) {
        if (Objects.requireNonNull(response.getStatusCode()) == Status.OK) {
            System.out.println(response.getMessage());
        } else {
            System.out.println("[" + response.getStatusCode() + "]: " + response.getMessage());
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

        Response visit(Exit exit);
    }
}
