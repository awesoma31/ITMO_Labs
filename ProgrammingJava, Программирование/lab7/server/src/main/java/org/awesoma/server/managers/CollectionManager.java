package org.awesoma.server.managers;

import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.Movie;
import org.awesoma.server.util.IDGenerator;
import org.awesoma.server.util.json.DumpManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Vector;

public class CollectionManager {
    private final IDGenerator idGenerator;
    private final LocalDateTime initDate = LocalDateTime.now();
    private Vector<Movie> collection;

    public CollectionManager(DumpManager dumpManager) throws ValidationException, IOException {
        this.collection = dumpManager.readCollection();

        idGenerator = new IDGenerator(this.collection);

        idGenerator.initIDs();
    }

    public void updateIDs() {
        idGenerator.initIDs();
    }

    public void updateCreationDate() {
        for (Movie m : collection) {
            if (m.getCreationDate() == null) {
                m.setCreationDate(LocalDateTime.now());
            }
        }
    }

    public void update() {
        updateCreationDate();
        updateIDs();
    }

    public Vector<Movie> getCollection() {
        this.collection.sort(Movie::compareTo);
        return this.collection;
    }

    public void setCollection(Vector<Movie> collection) {
        this.collection = collection;
    }

    public void addMovie(Movie m) {
        collection.add(m);
        update();
    }

    public void clearCollection() {
        collection.clear();
    }

    public void sortCollection() {
        update();
        Collections.sort(collection);
    }

    public LocalDateTime getInitDate() {
        return initDate;
    }

    public void removeByIndex(int index) {
        collection.remove(index);
    }
}
