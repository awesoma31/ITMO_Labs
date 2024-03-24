package awesoma.common.exceptions;

import awesoma.common.exceptions.models.Movie;

import java.util.HashSet;
import java.util.Vector;

/**
 * Class that is responsible for generating unique ids to the collection's movies
 */
public class UniqueIdGenerator {
    private final HashSet<Integer> idList;
    private int ID = 1;

    public UniqueIdGenerator(HashSet<Integer> idList) {
        this.idList = idList;
    }


    /**
     * identifies unique ids that are represented in the collection
     *
     * @param collection where to find ids
     * @return HashSet of unique ids that are represented in the collection
     */
    public static HashSet<Integer> identifyIds(Vector<Movie> collection) {
        HashSet<Integer> uniqueIds = new HashSet<>();
        for (Movie m : collection) {
            uniqueIds.add(m.getId());
        }
        return uniqueIds;
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

    public HashSet<Integer> getIdList() {
        return idList;
    }
}
