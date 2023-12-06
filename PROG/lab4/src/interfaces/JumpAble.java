package interfaces;

import enums.Places;
import heroes.AbstractHero;

public interface JumpAble {
    default void jump(AbstractHero who, Places where) {
        System.out.println(who.name + " перепрыгнул через " + where);
    }
}
