package org.awesoma.common;

import org.awesoma.common.commands.*;

import java.util.HashMap;

public class Environment {
    public static final int PORT = 8000;
    public static final String HOST = "localhost";
    public static final String ENV = "lab7";
    //    private final HashMap<String, Command> availableCommandsNS = new HashMap<>();
    private static final HashMap<String, Command> AVAILABLE_COMMANDS = new HashMap<>();

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

        AVAILABLE_COMMANDS.put(HelpCommand.name, new HelpCommand());
        AVAILABLE_COMMANDS.put(ShowCommand.NAME, new ShowCommand());
        AVAILABLE_COMMANDS.put(ExitCommand.NAME, new ExitCommand());
        AVAILABLE_COMMANDS.put(AddCommand.NAME, new AddCommand());
        AVAILABLE_COMMANDS.put(InfoCommand.NAME, new InfoCommand());
        AVAILABLE_COMMANDS.put(ClearCommand.name, new ClearCommand());
        AVAILABLE_COMMANDS.put(SortCommand.name, new SortCommand());
        AVAILABLE_COMMANDS.put(PrintFieldAscendingTBOCommand.name, new PrintFieldAscendingTBOCommand());
        AVAILABLE_COMMANDS.put(UpdateIdCommand.NAME, new UpdateIdCommand());
        AVAILABLE_COMMANDS.put(RemoveByIdCommand.NAME, new RemoveByIdCommand());
        AVAILABLE_COMMANDS.put(RemoveAtCommand.NAME, new RemoveAtCommand());
        AVAILABLE_COMMANDS.put(AddIfMaxCommand.NAME, new AddIfMaxCommand());
    }

//    public HashMap<String, Command> getAvailableCommandsNS() {
//        return availableCommandsNS;
//    }
}
