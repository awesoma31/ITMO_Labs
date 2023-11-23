package src.heroes.abstractClasses;


import src.enums.Languages;
import src.interfaces.UgovarivAble;

public abstract class AbstractHero implements UgovarivAble {
    protected String name;
    protected String roditName;
    protected Languages language;

    // уметь уговаривать кто на каком (человеческом, тигрином)
    public AbstractHero(String name) {
        this.name = name;
    }

    public AbstractHero(String name, String roditName) {
        this.name = name;
        this.roditName = roditName;
    }

    public AbstractHero(String name, String roditName, Languages lang) {
        this.name = name;
        this.roditName = roditName;
        language = lang;
    }

    public abstract String persuade(AbstractHero who);

//    public Languages getLanguage() {
//        return this.language;
//    }

    public abstract String getName();

    public String getRoditName() {
        return roditName;
    }
}
