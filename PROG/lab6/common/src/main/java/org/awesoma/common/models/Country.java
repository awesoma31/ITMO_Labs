package org.awesoma.common.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Enum represents available nationalities
 */
public enum Country implements Serializable {
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
