package src.enums;

public enum POTS {
    ONE("одну"),
    OTHER("другую");

    private final String name;

    POTS(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public POTS[] getVals() {
        return POTS.values();
    }
}
