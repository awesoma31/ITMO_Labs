package heroes;

import enums.Languages;
import enums.Volume;
import heroes.abstractClasses.AbstractHero;
import interfaces.JumpAble;
import interfaces.SpeakAble;
import interfaces.UnderstandAble;

import java.util.Objects;

public class Kenga extends AbstractHero implements SpeakAble, UnderstandAble, JumpAble {
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
    public void say(String s, AbstractHero hero, Volume volume) {
        System.out.println(name + " сказала: " + s + volume.name());
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }

    @Override
    public void understand(String s) {
        System.out.println(name + " понял " + s);
    }

    @Override
    public void jump(String s) {
        System.out.println("подскочила от " + s);
    }

    public void catchSpoon(boolean status) {
        final boolean isCatched = status;

        class Spoon {
            public void printStatus() {
                if (isCatched) {
                    System.out.println(name + " схватила ложку");
                } else {
                    System.out.println(name + "не схватила ложку");
                }

            }
        }

        Spoon spoon = new Spoon();
        spoon.printStatus();
    }
}
