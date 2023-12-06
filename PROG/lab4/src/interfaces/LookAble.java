package interfaces;

import enums.Moods;
import heroes.AbstractHero;

public interface LookAble {
    default void look(AbstractHero who, Moods mood) {
        System.out.println(who.name + " посмотрел " + mood);
    }
}
