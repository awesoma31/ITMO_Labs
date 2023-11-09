package src.enums;

public enum BodyParts {
    NOSE("Нос"), PAW("Лапа");

    private String name;

    BodyParts(String n) {
        name = n;
    }

    String getName() {
        return name;
    }
}
