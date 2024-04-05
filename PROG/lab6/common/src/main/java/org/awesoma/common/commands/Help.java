package org.awesoma.common.commands;

import org.awesoma.common.Environment;
import org.awesoma.common.interaction.Request;
import org.awesoma.common.interaction.Response;
import org.awesoma.common.interaction.Status;

import java.util.ArrayList;
import java.util.List;

public class Help extends AbstractCommand {
    public Help() {
        super("help", "shows available commands");
    }

    @Override
    public Request buildRequest(List<String> args) {
        return new Request(this.getName());
    }

//    @Override
//    public Response execute(ArrayList<String> args) {
//        return new Response(Status.OK);
//    }

    @Override
    public void handleResponse(Response response) {
        // todo может строку билдить на серваке и посылать, хотя нахуя?
        if (response.getStatusCode() == Status.OK) {
            Environment.availableCommands.values().stream()
                    .map(Command::getHelp)
                    .forEach(System.out::println);
        } else if (response.getStatusCode() == Status.ERROR) {
            System.out.println("Error executing " + this.getName() + " command");
        }
    }

    @Override
    public Response accept(Visitor visitor, Request request) {
        return visitor.visit(this);
    }
}
