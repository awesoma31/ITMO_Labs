package org.awesoma.server.managers;

import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.Movie;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Vector;

/**
 * This class represents main operations with collection in memory
 */
public class CollectionManager {
    private final LocalDateTime initDate = LocalDateTime.now();
    private Vector<Movie> collection;

    public CollectionManager() throws ValidationException, IOException {
        this.collection = new Vector<>();
    }

    public Vector<Movie> getCollection() {
        this.collection.sort(Movie::compareTo);
        return this.collection;
    }

    public void setCollection(Vector<Movie> collection) {
        this.collection = collection;
    }

    public void clearCollection() {
        collection.clear();
    }

    public void sortCollection() {
        Collections.sort(collection);
    }

    public LocalDateTime getInitDate() {
        return initDate;
    }
}
