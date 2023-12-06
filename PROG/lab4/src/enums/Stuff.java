package enums;

public enum Stuff {
    THINGS("вещей"),
    ALL("все"),
    LAMP("лампа"),
    OSCAR_WALLET("бумажник Оскара"),
    SOUP_BOWL("миска супа"),
    OSCAR_CLOCKS("часы Оскара");

    private final String name;

    Stuff(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
