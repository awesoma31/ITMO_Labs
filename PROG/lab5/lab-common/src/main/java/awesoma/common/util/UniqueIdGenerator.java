package awesoma.common.util;

import java.util.HashSet;

public class UniqueIdGenerator {
    private final HashSet<Integer> idList;
    private int ID = 1;

    public UniqueIdGenerator(HashSet<Integer> idList) {
        this.idList = idList;
    }

    public UniqueIdGenerator(HashSet<Integer> idList, int minId) {
        this.idList = idList;
        this.ID = minId;
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
