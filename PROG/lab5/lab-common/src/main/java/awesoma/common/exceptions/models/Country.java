package awesoma.common.exceptions.models;

import java.util.ArrayList;

/**
 * Enum represents available nationalities
 */
public enum Country {
    UNITED_KINGDOM,
    GERMANY,
    FRANCE;

    public static ArrayList<String> getVals() {
        ArrayList<String> vals = new ArrayList<>();
        for (Country g : Country.values()) {
            vals.add(g.name());
        }
        return vals;
    }
}
