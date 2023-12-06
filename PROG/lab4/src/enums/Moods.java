package enums;

public enum Moods {
    ANGRILY("злобно"),
    BORED("скучно"),
    STARING("уставившись"),
    AMAZED("удивлен");

    private final String name;

    Moods(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
