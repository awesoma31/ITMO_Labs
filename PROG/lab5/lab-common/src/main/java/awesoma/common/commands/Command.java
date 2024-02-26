package awesoma.common.commands;

/**
 * Abstract class that represents command being
 */
public abstract class Command implements ExecutAble {
    public static final int argAmount = 0;

    private final String description;
    private String name;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
//        this.commandManager = commandManager;
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
}

