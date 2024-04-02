package org.awesoma.common.commands;

import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.StatusCode;
import org.awesoma.common.models.Movie;

import java.util.ArrayList;

public class Help extends AbstractCommand{
    public Help() {
        super("help", "shows available commands");
    }

    @Override
    public Request buildRequest() {
        return new Request(this.getName());
    }

    @Override
    public Response execute(ArrayList<String> args) {
        return new Response(StatusCode.OK);
    }

    @Override
    public void handleResponse(Response response) {
        if (response.getStatusCode() == StatusCode.OK) {
            AbstractCommand.availableCommands.values().stream()
                    .map(Command::getHelp)
                    .forEach(System.out::println);
        } else if (response.getStatusCode() == StatusCode.ERROR) {
            System.out.println("Error executing " + this.getName() + " command");
        }
    }
}
