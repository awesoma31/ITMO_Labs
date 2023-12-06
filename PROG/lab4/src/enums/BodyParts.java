package enums;

public enum BodyParts {
    NOSE("нос"),
    PAW("лапа"),
    ARM("рука"),
    ;

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
