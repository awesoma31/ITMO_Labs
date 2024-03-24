package org.awesoma.common;

import org.awesoma.common.models.Movie;

import java.io.Serializable;

public class Response implements Serializable {
    private final StatusCode statusCode;
    private final String message;
    private final Movie movie;


    public Response(StatusCode statusCode, String message, Movie movie) {
        this.statusCode = statusCode;
        this.message = message;
        this.movie = movie;
    }

    @Override
    public String toString() {
        return "Server response " +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", movie=" + movie;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
