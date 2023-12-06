package interfaces;

import heroes.AbstractHero;

public interface SayAble {
    default void say(AbstractHero hero, String s) {
        System.out.println(hero.name + " сказал: " + s);
    }
}
