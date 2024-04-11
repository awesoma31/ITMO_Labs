package org.awesoma.common;

import org.awesoma.common.commands.*;

import java.util.HashMap;

public class Environment {
    public static final HashMap<String, Command> availableCommands = new HashMap<>();
    public static final int PORT = 8000;
    public static final String HOST = "localhost";
    public static final String ENV = "lab7";


    static {
        availableCommands.put(Help.name, new Help());
        availableCommands.put(Show.NAME, new Show());
        availableCommands.put(Exit.NAME, new Exit());
        availableCommands.put(Add.NAME, new Add());
        availableCommands.put(Info.NAME, new Info());
        availableCommands.put(Clear.name, new Clear());
        availableCommands.put(Sort.name, new Sort());
        availableCommands.put(PrintFieldAscendingTBO.name, new PrintFieldAscendingTBO());
        availableCommands.put(UpdateId.NAME, new UpdateId());
        availableCommands.put(RemoveById.NAME, new RemoveById());
        availableCommands.put(RemoveAt.NAME, new RemoveAt());
        availableCommands.put(AddIfMax.NAME, new AddIfMax());
    }
}
