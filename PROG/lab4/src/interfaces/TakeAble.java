package interfaces;

import enums.BodyParts;
import enums.Places;
import enums.Pockets;
import enums.Stuff;

public interface TakeAble {
    default void take(Stuff obj, Places place) {
        System.out.println("взял " + obj + " из " + place);
    }

    default void take(Stuff obj, Pockets place) {
        System.out.println("взял " + obj + " из " + place);
    }

    default void take(Stuff obj, BodyParts bp) {
        if (bp == BodyParts.ARM) {
            System.out.println("взял " + obj + " в " + bp);
        }

    }
}
