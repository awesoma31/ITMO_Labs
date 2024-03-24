package org.awesoma.server.commands;

public abstract class AbstractServerCommand implements Command{
    private String name;
    protected AbstractServerCommand(String name) {
        this.name = name;
    }

    public AbstractServerCommand() {
    }

    public String getName() {
        return name;
    }
}
