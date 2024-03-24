package org.awesoma.common;

import org.awesoma.common.models.Movie;

import java.io.Serializable;

public class Response implements Serializable {
    private final StatusCode statusCode;
    private final String message;
    private final Object extraData;


    public Response(StatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.extraData = null;
    }

    public Response(StatusCode statusCode, String message, Object extraData) {
        this.statusCode = statusCode;
        this.message = message;
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return "Server response " +
                "statusCode=" + statusCode +
                ", message='" + message + '\'';
//                ", movie=" + movie;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getExtraData() {
        return extraData;
    }
}
