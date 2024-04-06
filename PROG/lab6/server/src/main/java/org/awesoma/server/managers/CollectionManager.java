package org.awesoma.server.managers;

import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;
import org.awesoma.common.util.json.DumpManager;
import org.awesoma.server.util.IDGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Vector;

public class CollectionManager {
    private final Vector<Movie> collection;
    private final IDGenerator idGenerator;
    private final LocalDateTime initDate = LocalDateTime.now();
    private final DumpManager dumpManager;

    public CollectionManager(Vector<Movie> collection) {
        // todo
        this.collection = collection;
        idGenerator = new IDGenerator(this.collection);
        dumpManager = null;

        idGenerator.initIDs();
    }

    public CollectionManager(DumpManager dumpManager) throws ValidationException, IOException {
        this.dumpManager = dumpManager;
        this.collection = dumpManager.readCollection();

//        addSampleMovies(collection);

        idGenerator = new IDGenerator(this.collection);

        idGenerator.initIDs();
    }

    private static void addSampleMovies(Vector<Movie> collection) {
        try {
            collection.add(
                    new Movie(
                            3,
                            "Mamba",
                            20,
                            30,
                            43L,
                            new Coordinates(3, 4),
                            LocalDateTime.now(),
                            MovieGenre.COMEDY,
                            new Person(
                                    "Jopik",
                                    LocalDateTime.now(),
                                    34f,
                                    Color.RED,
                                    Country.FRANCE
                            )
                    )
            );
            collection.add(
                    new Movie(
                            2,
                            "Jango",
                            40,
                            20,
                            54L,
                            new Coordinates(2, 1),
                            LocalDateTime.now(),
                            MovieGenre.COMEDY,
                            new Person(
                                    "Jopik",
                                    LocalDateTime.now(),
                                    34f,
                                    Color.RED,
                                    Country.FRANCE
                            )
                    )
            );
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
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

    public void addMovie(Movie m) {
        collection.add(m);
        update();
    }

    public LocalDateTime getInitDate() {
        return initDate;
    }

    // todo save read ...
}
