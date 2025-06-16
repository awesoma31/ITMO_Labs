package src.enums;

public enum Stuff {
    THINGS("вещей"), ALL("все");

    private final String name;

    Stuff(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
