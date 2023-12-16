package heroes;

import enums.Languages;
import heroes.abstractClasses.AbstractHero;
import interfaces.SpeakAble;

import java.util.Objects;

public class Kenga extends AbstractHero implements SpeakAble {
    public Kenga(String name, String roditName, Languages lang) {
        super(name, roditName, lang);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kenga that = (Kenga) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "Кенгу";
    }

    @Override
    public void speak(String str) {
        System.out.print(" - а Кенга говорит: \"" + str + "\".");
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }
}
