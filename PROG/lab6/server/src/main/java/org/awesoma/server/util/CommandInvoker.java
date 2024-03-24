package org.awesoma.server.util;

import org.awesoma.common.Request;
import org.awesoma.common.Response;
import org.awesoma.common.StatusCode;
import org.awesoma.server.commands.AbstractServerCommand;
import org.awesoma.server.commands.ClearCommand;
import org.awesoma.server.commands.Command;
import org.awesoma.server.commands.ShowCommand;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandInvoker {

    private HashMap<String, Command> commands = new HashMap<>();


    public CommandInvoker() {
    }

    public Response invoke(Request request) {
        if (request.getCommandName().equals("show")) {
            new ShowCommand().execute(request.getArgs(), request.getMovie());
            return new Response(StatusCode.OK, request.getCommandName() + " executed", null);
        }
        return new Response(StatusCode.ERROR, request.getCommandName() + " didnt executed", null);
    }
}
