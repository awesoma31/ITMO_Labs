package org.awesoma.common;

import org.awesoma.common.commands.*;

import java.util.HashMap;

public class Environment {
    public static final int PORT = 8000;
    public static final String HOST = "localhost";
    public static final String ENV = "lab7";
    private static final HashMap<String, Command> AVAILABLE_COMMANDS = new HashMap<>();

    // GOVNOCODE
    static {
        registerCommands();
    }

    public static HashMap<String, Command> getAvailableCommands() {
        return AVAILABLE_COMMANDS;
    }

    public static void register(Command command) {
        AVAILABLE_COMMANDS.put(command.getName(), command);
    }

    public static void registerCommands() {
        register(new HelpCommand());
        register(new ShowCommand());
        register(new ExitCommand());
        register(new AddCommand());
        register(new InfoCommand());
        register(new ClearCommand());
        register(new SortCommand());
        register(new PrintFieldAscendingTBOCommand());
        register(new UpdateIdCommand());
        register(new RemoveAtCommand());
        register(new RemoveByIdCommand());
        register(new AddIfMaxCommand());
    }
}
