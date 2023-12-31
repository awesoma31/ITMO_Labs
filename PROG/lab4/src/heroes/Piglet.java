package heroes;

import enums.Languages;
import heroes.abstractClasses.AbstractHero;
import interfaces.SpeakAble;

import java.util.Objects;

public class Piglet extends AbstractHero implements SpeakAble {

    public Piglet(String name, String roditName, Languages lang) {
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
        Piglet that = (Piglet) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "Piglet{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }

    @Override
    public void say(AbstractHero hero, String str) {
        System.out.println(name + " сказал " + hero.getName() + str);
    }
}
