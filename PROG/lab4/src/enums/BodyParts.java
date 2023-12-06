package src.enums;

public enum BodyParts {
    NOSE("нос"), PAW("лапу");

    private final String name;

    BodyParts(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BodyParts[] getVals() {
        return values();
    }
}
