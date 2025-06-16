package enums;

public enum Stuff {
    THINGS("вещей"), ALL("все"), POT("банка"), CHAIR_BACK("спинка кресла Ру");

    private final String name;

    Stuff(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
