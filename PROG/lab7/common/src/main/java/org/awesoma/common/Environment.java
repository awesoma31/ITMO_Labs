package org.awesoma.common;

import org.awesoma.common.commands.*;

import java.util.HashMap;

public class Environment {
//    private final HashMap<String, Command> availableCommandsNS = new HashMap<>();
    private static final HashMap<String, Command> AVAILABLE_COMMANDS = new HashMap<>();
    public static final int PORT = 8000;
    public static final String HOST = "localhost";
    public static final String ENV = "lab7";

    static {
        registerCommands();
    }

    public static HashMap<String, Command> getAvailableCommands() {
        return AVAILABLE_COMMANDS;
    }

    public static void register(AbstractCommand command) {
        AVAILABLE_COMMANDS.put(command.getName(), command);
    }

    public static void registerCommands() {

        AVAILABLE_COMMANDS.put(Help.name, new Help());
        AVAILABLE_COMMANDS.put(Show.NAME, new Show());
        AVAILABLE_COMMANDS.put(Exit.NAME, new Exit());
        AVAILABLE_COMMANDS.put(Add.NAME, new Add());
        AVAILABLE_COMMANDS.put(Info.NAME, new Info());
        AVAILABLE_COMMANDS.put(Clear.name, new Clear());
        AVAILABLE_COMMANDS.put(Sort.name, new Sort());
        AVAILABLE_COMMANDS.put(PrintFieldAscendingTBO.name, new PrintFieldAscendingTBO());
        AVAILABLE_COMMANDS.put(UpdateId.NAME, new UpdateId());
        AVAILABLE_COMMANDS.put(RemoveById.NAME, new RemoveById());
        AVAILABLE_COMMANDS.put(RemoveAt.NAME, new RemoveAt());
        AVAILABLE_COMMANDS.put(AddIfMax.NAME, new AddIfMax());
    }

//    public HashMap<String, Command> getAvailableCommandsNS() {
//        return availableCommandsNS;
//    }
}
