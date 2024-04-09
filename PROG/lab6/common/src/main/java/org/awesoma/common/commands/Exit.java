package org.awesoma.common.commands;

import org.awesoma.common.network.Request;
import org.awesoma.common.network.Response;

import java.util.ArrayList;

public class Exit extends AbstractCommand {
    public static final String NAME = "exit";

    public Exit() {
        super(Exit.NAME, "stops client app");
    }

    @Override
    public Request buildRequest(ArrayList<String> args) {
        // todo close connection
        return new Request(this.name);
    }

    @Override
    public void handleResponse(Response response) {
        System.out.println("Exiting");
        System.exit(1);
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
