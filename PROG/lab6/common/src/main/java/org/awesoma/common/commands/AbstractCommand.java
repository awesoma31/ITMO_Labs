package org.awesoma.common.commands;

import org.awesoma.common.Request;
import org.awesoma.common.Response;

import java.util.ArrayList;
import java.util.HashMap;

public class AbstractCommand implements Command{
    private static final HashMap<String, Command> availableCommands = new HashMap<>();

    private String name;
    private String description;

    static {
        availableCommands.put("", new AbstractCommand());
    }

    @Override
    public Request build() {
        return null;
    }

    @Override
    public Response execute(ArrayList<String> args) {
        return null;
    }
}
