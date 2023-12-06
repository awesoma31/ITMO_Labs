package interfaces;

import enums.Places;
import heroes.AbstractHero;

public interface WalkAble {
    default void walk(AbstractHero who, Places where) {
        System.out.println(who.name + " пошел в " + where);
        who.position = where;
    }
}
