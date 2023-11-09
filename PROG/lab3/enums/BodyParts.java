package enums;

public enum BodyParts {
    NOSE ("Нос"),
    PAW ("Лапа");

    private String bodyPart;

    BodyParts(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    @Override
    public String toString() {
        return "body part - " + bodyPart;
    }
}
