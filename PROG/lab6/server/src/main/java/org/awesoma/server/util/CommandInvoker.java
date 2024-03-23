package org.awesoma.server;

import org.awesoma.server.commands.ClearCommand;
import org.awesoma.server.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandInvoker {
    private final ClearCommand clearCommand;

    private HashMap<String, Command> commands = new HashMap<>();


    public CommandInvoker(ClearCommand clearCommand) {
        this.clearCommand = clearCommand;

        commands.put(clearCommand.getName(), clearCommand);
    }


    public void clear(ArrayList<String> args) {
        clearCommand.execute(args);
    }

    public void executeCommand(Command command, ArrayList<String> args) {

    }

}
