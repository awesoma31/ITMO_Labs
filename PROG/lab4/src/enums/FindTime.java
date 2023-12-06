package src.enums;

public enum FindTime {
    PAST("находил"), PRESENT("нашел");

    private final String name;

    FindTime(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
