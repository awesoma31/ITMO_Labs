package src.heroes.abstractClasses;


import src.interfaces.UgovarivAble;

public abstract class AbstractHero implements UgovarivAble {
    protected String name;
    protected String roditName;

    // уметь уговаривать кто на каком (человеческом, тигрином)

    public AbstractHero(String name) {
        this.name = name;
    }

    public AbstractHero(String name, String roditName) {
        this.name = name;
        this.roditName = roditName;
    }

    public  abstract String getName();

    public String getRoditName() {
        return roditName;
    }
}
