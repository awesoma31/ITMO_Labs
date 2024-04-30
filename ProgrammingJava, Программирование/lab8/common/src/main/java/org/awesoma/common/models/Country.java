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

    public static ArrayList<String> getValues() {
        ArrayList<String> values = new ArrayList<>();
        for (Country g : Country.values()) {
            values.add(g.name());
        }
        return values;
    }
}
