package enums;

public enum Places {
    UNDER_TABLE("под столом"),
    HALLWAY("прихожая"),
    ROOM("комната"),
    NEAR_LAMP("рядом с лампой"),
    WINDOWSILL("подоконник");

    private final String name;

    Places(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
