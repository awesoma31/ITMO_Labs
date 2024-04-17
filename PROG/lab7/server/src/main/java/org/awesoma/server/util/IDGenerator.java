package org.awesoma.server.util;

import org.awesoma.common.models.Movie;

import java.util.HashSet;
import java.util.Vector;

/**
 * Class that is responsible for generating unique ids to the collection's movies
 */
public class IDGenerator {
    private final HashSet<Integer> idList = new HashSet<>();
    private final Vector<Movie> collection;
    private int ID = 1;


    //todo id list update after deleting an element
    public IDGenerator(Vector<Movie> collection) {
        this.collection = collection;
        this.identifyIds();
    }

    public void identifyIds() {
        for (Movie m : this.collection) {
            idList.add(m.getId());
        }
    }

    public void initIDs() {
        for (Movie m : collection) {
            if (m.getId() == null) {
                m.setId(generateUniqueId());
            }
        }
    }

    /**
     * @return a unique id< not represented in the identified unique ids
     */
    public int generateUniqueId() {
        while (idList.contains(ID)) {
            ID++;
        }
        idList.add(ID);
        return ID;
    }

}
