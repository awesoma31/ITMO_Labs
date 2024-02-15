package awesoma.common.commands;

import awesoma.common.managers.CommandManager;
import awesoma.common.models.Movie;

import java.util.HashMap;
import java.util.TreeSet;

public abstract class Command {
    private String name;
    private String description;
//    private final CommandManager commandManager;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
//        this.commandManager = commandManager;
    }

    public void register(CommandManager commandManager) {
        commandManager.register(this);
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

