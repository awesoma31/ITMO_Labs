package org.awesoma.common.models;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Enum represents genre of the movie
 */
public enum MovieGenre implements Serializable {
    COMEDY,
    MUSICAL,
    HORROR;


    public static ArrayList<String> getValues() {
        ArrayList<String> values = new ArrayList<>();
        for (MovieGenre g : MovieGenre.values()) {
            values.add(g.name());
        }
        return values;
    }
}
