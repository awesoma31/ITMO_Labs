package awesoma.common.util;

import awesoma.common.models.Movie;

import java.util.HashSet;
import java.util.Vector;

public class UniqueIdGenerator {
    private final HashSet<Integer> idList;
    private int ID = 1;

    public UniqueIdGenerator(HashSet<Integer> idList) {
        this.idList = idList;
    }


    public static HashSet<Integer> identifyIds(Vector<Movie> collection) {
        HashSet<Integer> uniqueIds = new HashSet<>();
        for (Movie m : collection) {
            uniqueIds.add(m.getId());
        }
        return uniqueIds;
    }

    public int generateUniqueId() {
        while (idList.contains(ID)) {
            ID++;
        }
        return ID;
    }

    public HashSet<Integer> getIdList() {
        return idList;
    }
}
