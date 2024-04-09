package org.awesoma.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private final Status status;
    private final String message;
    private final Object extraData;

    public Response(Status status) {
        this.status = status;
        this.message = null;
        this.extraData = null;
    }

    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
        this.extraData = null;
    }

    public Response(Status status, String message, Object extraData) {
        this.status = status;
        this.message = message;
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return "Server response: " +
                "statusCode = " + status +
                ", message = '" + message + '\'' +
                ", extraData = " + extraData
                ;
//                ", movie=" + movie;
    }

    public Status getStatusCode() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getExtraData() {
        return extraData;
    }
}
