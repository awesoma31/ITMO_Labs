package src.heroes.abstractClasses;


public abstract class AbstractHero {
    protected String name;
    protected String roditName;

    public AbstractHero(String name) {
        this.name = name;
    }

    public AbstractHero(String name, String roditName) {
        this.name = name;
        this.roditName = roditName;
    }

    public String getName() {
        return name;
    }

    public String getRoditName() {
        return roditName;
    }
}
