package interfaces;

import enums.Volume;
import heroes.abstractClasses.AbstractHero;

public interface SpeakAble {
    default void speak(String whatToSay) {
        System.out.println(whatToSay);
    }

    default void say(AbstractHero hero, String str) {
        System.out.println(hero.getName() + " сказал " + str);
    }

    default void say(String str) {
        System.out.println("сказал " + str);
    }

    default void tell(AbstractHero whom, String s) {
        System.out.println("рассказал " + whom + ": " + s);
    }

    default void say(String s, AbstractHero hero, Volume volume) {
        System.out.println("сказал " + s + hero.getName() + volume);
    }
}
