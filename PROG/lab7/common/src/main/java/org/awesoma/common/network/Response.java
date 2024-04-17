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
        if (message == null) {
            return "[RESPONSE]: status code " + status;
        } else {
            return "[RESPONSE]: status code " + status + ": " + message;
        }
    }

    public Status getStatusCode() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
