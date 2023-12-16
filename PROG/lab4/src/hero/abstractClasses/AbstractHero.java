package heroes.abstractClasses;


import enums.Languages;
import interfaces.HeroAble;
import interfaces.UgovarivAble;

public abstract class AbstractHero implements HeroAble {
    protected String name;
    protected String roditName;
    protected Languages language;

    public AbstractHero() {
    }

    public AbstractHero(String name) {
        this.name = name;
    }

    public AbstractHero(String name, String roditName) {
        this.name = name;
        this.roditName = roditName;
    }

    public AbstractHero(String name, Languages language) {
        this.name = name;
        this.language = language;
    }

    public AbstractHero(String name, String roditName, Languages lang) {
        this.name = name;
        this.roditName = roditName;
        language = lang;
    }
}
