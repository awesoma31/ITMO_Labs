package src.enums;

public enum Pots {
    ONE("одну"),
    OTHER("другую");

    private final String name;

    Pots(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Pots[] getVals() {
        return Pots.values();
    }
}
