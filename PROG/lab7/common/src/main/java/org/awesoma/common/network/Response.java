package org.awesoma.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private final Status status;
    private final String message;

    public Response(Status status) {
        this.status = status;
        this.message = null;
    }

    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Server response: " +
                "statusCode = " + status +
                ", message = '" + message;
    }

    public Status getStatusCode() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
