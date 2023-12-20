package interfaces;

import enums.FindTime;
import enums.Stuff;

public interface FindAble {
    default void find(FindTime time, Stuff stuff) {
        System.out.println("нашел " + stuff + time);
    }
    default void find(Stuff stuff) {
        System.out.println("нашел " + stuff);
    }
}
