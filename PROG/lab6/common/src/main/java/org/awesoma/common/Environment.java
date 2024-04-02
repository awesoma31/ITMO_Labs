package org.awesoma.common;

import org.awesoma.common.commands.Command;
import org.awesoma.common.commands.Exit;
import org.awesoma.common.commands.Help;
import org.awesoma.common.commands.Show;

import java.util.HashMap;

public class Environment {
    public static final HashMap<String, Command> availableCommands = new HashMap<>();

    static {
        availableCommands.put("help", new Help());
        availableCommands.put("show", new Show());
        availableCommands.put("exit", new Exit());
    }
}
