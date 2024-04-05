package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;

import java.util.ArrayList;
import java.util.List;

public class Exit extends AbstractCommand {
    public Exit() {
        super("exit", "stops client app");
    }

    @Override
    public Request buildRequest(List<String> args) {
        // todo close connection
        System.exit(0);
        return new Request(this.name);
    }

    @Override
    public void handleResponse(Response response) {}

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
