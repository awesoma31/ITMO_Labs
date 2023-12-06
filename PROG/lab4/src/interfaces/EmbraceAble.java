package interfaces;

import heroes.AbstractHero;

public interface EmbraceAble {
    default void embrace(AbstractHero who, AbstractHero whom) {
        System.out.println(who.name + " обнял " + whom.name);
        whom.isEmbraced = true;
    }

    default void unEmbrace(AbstractHero who, AbstractHero whom) {
        System.out.println(who.name + " разобнял " + whom.name);
        whom.isEmbraced = false;
    }
}
