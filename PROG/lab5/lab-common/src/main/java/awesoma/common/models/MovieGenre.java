package awesoma.common.models;


import java.util.ArrayList;

public enum MovieGenre {
    COMEDY,
    MUSICAL,
    HORROR;

    public static ArrayList<String> getVals() {
        ArrayList<String> vals = new ArrayList<>();
        for (MovieGenre g : MovieGenre.values()) {
            vals.add(g.name());
        }
        return vals;
    }
}
