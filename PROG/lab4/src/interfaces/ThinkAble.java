package interfaces;

public interface ThinkAble {
    default void think(String s) {
        System.out.println("думает: " + s);
    }
}
