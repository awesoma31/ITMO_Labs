package src.heroes;

import src.enums.Languages;
import src.heroes.abstractClasses.AbstractHero;
import src.interfaces.SpeakAble;

import java.util.Objects;

public class KroshkaRu extends AbstractHero implements SpeakAble {
    public KroshkaRu(String name, String roditName, Languages lang) {
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
        KroshkaRu that = (KroshkaRu) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "KroshkaRu{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }

    @Override
    public void speak(String str) {
        System.out.print(" И " + this.name + " говорит: \"" + str + "\"");
    }
}
