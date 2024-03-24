package org.awesoma.server.commands;

public abstract class AbstractServerCommand implements Command{
    private final String name;
    protected AbstractServerCommand(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
