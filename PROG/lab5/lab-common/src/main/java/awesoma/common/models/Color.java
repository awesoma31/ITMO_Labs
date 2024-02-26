package awesoma.common.models;

import java.util.ArrayList;

/**
 * Enum represents available colors
 */
public enum Color {
    RED,
    BLACK,
    YELLOW,
    ORANGE,
    WHITE;

    public static ArrayList<String> getVals() {
        ArrayList<String> vals = new ArrayList<>();
        for (Color g : Color.values()) {
            vals.add(g.name());
        }
        return vals;
    }
}
