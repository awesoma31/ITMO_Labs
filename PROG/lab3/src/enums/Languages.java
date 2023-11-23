package src.enums;

public enum Languages {
    TIGERLANG("тигрином"), HUMANLANG ("человеческом"), PIGLETLANG("пятачковском"), KANGAROOOLANG("кенгурушном");

    private final String title;
    Languages(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }
}
