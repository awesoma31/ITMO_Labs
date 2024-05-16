package org.awesoma.common.network;

import org.awesoma.common.models.Movie;

import java.io.Serializable;
import java.util.Vector;

/**
 * Server response to client
 */
public class Response implements Serializable {
    private final Status status;
    private final String message;
    private final Vector<Movie> collection;

    public Response(Status status) {
        this.status = status;
        this.message = null;
        collection = null;
    }

    public Response(Status status, String message, Vector<Movie> collection) {
        this.status = status;
        this.message = message;
        this.collection = collection;
    }

    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
        collection = null;
    }

    public Response(Status status, Vector<Movie> collection) {
        this.status = status;
        this.message = null;
        this.collection = collection;
    }

    @Override
    public String toString() {
        return "[RESPONSE]: status -> " + status +
                (message == null ? null : (": " + message));
    }

    public Status getStatusCode() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Vector<Movie> getCollection() {
        return collection;
    }
}
