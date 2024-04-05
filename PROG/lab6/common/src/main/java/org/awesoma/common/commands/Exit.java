package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

import java.util.ArrayList;

public class Exit extends AbstractCommand {
    public static final String NAME = "exit";

    public Exit() {
        super(Exit.NAME, "stops client app");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        // todo close connection
        System.exit(0);
        return null;
    }

    @Override
    public void handleResponse(Response response) {
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        // todo exiting the programm
        return null;
    }

//    @Override
//    public Response execute(ArrayList<String> args) {
//        return new Response(Status.ERROR, "this command cant be invoked on server, how have you done that?!");
//    }
}
