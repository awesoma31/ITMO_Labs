package src.enums;

public enum BodyParts {
    NOSE("нос"),
    PAW("лапу");

    private String name;

    BodyParts(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
