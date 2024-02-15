package awesoma.common.commands;

import awesoma.common.managers.CommandManager;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getInfo() {
        return "Command name: " + this.name + ";\n Description: " + this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

