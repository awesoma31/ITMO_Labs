package interfaces;

import heroes.abstractClasses.AbstractHero;

public interface UnderstandAble {
    default void understand(AbstractHero hero, String s) {
        System.out.println(hero.getName() + " понял " + s);
    }
    default void understand(String s) {
        System.out.println("понял " + s);
    }
}
