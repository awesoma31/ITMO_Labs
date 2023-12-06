package enums;

public enum Pockets {
    FILLE_POCKET("карман Филле"),
    RULLE_POCKET("карман Рулле"),
    OSCAR_POCKET("карман Оскара"),
    KIDDO_POCKET("карман Малыша"),
    KARLSON_POCKET("карман Карлсона");

    private final String name;

    Pockets(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
