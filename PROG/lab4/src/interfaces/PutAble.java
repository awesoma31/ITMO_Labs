package interfaces;

import enums.BodyParts;
import enums.Places;
import enums.Pockets;
import enums.Stuff;

public interface PutAble {
    default void put(BodyParts bp, Places place) {
        System.out.println("сунул " + bp + " в " + place);
    }
    default void put(Stuff obj, Places place){
        System.out.println("положил " + obj + " в " + place);
    }

    default void put(Stuff obj, Pockets place){
        System.out.println("положил " + obj + " в " + place);
    }

    default void put(BodyParts obj, Pockets pocket){
        System.out.println("сунул" +
                " " + obj + " в " + pocket);
    }

    default void put(Stuff obj, Stuff stuff){
        System.out.println("положил " + obj + " в " + stuff);
    }
}
