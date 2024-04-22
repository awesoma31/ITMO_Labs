package src.heroes;

import src.enums.Languages;
import src.heroes.abstractClasses.AbstractHero;

import java.util.Objects;

public class ChristoferRobin extends AbstractHero {
    public ChristoferRobin(String name, String roditName, Languages lang) {
        super(name, roditName, lang);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChristoferRobin that = (ChristoferRobin) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "ChristoferRobin{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }
}
