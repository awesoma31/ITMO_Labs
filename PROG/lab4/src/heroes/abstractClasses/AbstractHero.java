package heroes.abstractClasses;


import enums.Languages;
import interfaces.UgovarivAble;

public abstract class AbstractHero implements UgovarivAble {
    protected String name;
    protected String roditName;
    protected Languages language;

    public AbstractHero(String name, String roditName, Languages lang) {
        this.name = name;
        this.roditName = roditName;
        language = lang;
    }

    public abstract String persuade(AbstractHero who);

    public abstract String getName();

    public String getRoditName() {
        return roditName;
    }
}
