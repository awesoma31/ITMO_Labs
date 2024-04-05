package org.awesoma.server.managers;

import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;
import org.awesoma.server.util.IDGenerator;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Vector;

public class CollectionManager {
    private final Vector<Movie> collection;
    private final IDGenerator idGenerator;
    private final LocalDateTime initDate = LocalDateTime.now();

    public CollectionManager(Vector<Movie> collection) {
        // todo
        this.collection = collection;
        idGenerator = new IDGenerator(this.collection);

        idGenerator.initIDs();
    }

    public CollectionManager() {
        this.collection = new Vector<>();

        addSampleMovies(collection);

        idGenerator = new IDGenerator(this.collection);

        idGenerator.initIDs();
    }

    public Vector<Movie> getCollection() {
        return this.collection;
    }

    private static void addSampleMovies(Vector<Movie> collection) {
        try {
            collection.add(
                    new Movie(
                            1,
                            "Mamba",
                            20,
                            30,
                            43L,
                            new Coordinates(3, 4),
                            LocalDateTime.now(),
                            MovieGenre.COMEDY,
                            new Person(
                                    "Jopik",
                                    new Date(),
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
                                    new Date(),
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

    public LocalDateTime getInitDate() {
        return initDate;
    }

    // todo save read ...
}
