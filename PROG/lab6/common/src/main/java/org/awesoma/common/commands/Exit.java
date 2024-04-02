package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;

public class Exit extends AbstractCommand{
    public Exit() {
        super("exit", "stops client app");
    }

    @Override
    public Request buildRequest() {
        // todo close connection
        return new Request(this.name);
    }

    @Override
    public void handleResponse(Response response) {

    }
}
