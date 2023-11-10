package src.heroes;

import src.heroes.abstractClasses.AbstractHero;
import src.interfaces.SpeakAble;

import java.util.Objects;

public class Kenga extends AbstractHero implements SpeakAble {
    public Kenga(String name) {
        super(name);
    }

    public Kenga(String name, String roditName) {
        super(name, roditName);
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
}
