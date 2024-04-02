package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.StatusCode;

import java.util.ArrayList;

public class Show extends AbstractCommand{
    public Show() {
        super("show", "Shows stored data");
    }

    @Override
    public Request buildRequest() {
        return new Request(getName());
    }

    @Override
    public Response execute(ArrayList<String> args) {
        return new Response(StatusCode.OK);
    }

    @Override
    public void handleResponse(Response response) {
        // todo
        System.out.println(name + " command executed successfully " + response.getStatusCode());
        System.out.println((String) response.getExtraData());
    }
}
