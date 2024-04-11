package org.awesoma.server.exceptions;

import java.io.IOException;

public class NoConnectionException extends IOException {
    public NoConnectionException() {
        super("No connection");
    }

    public NoConnectionException(String message) {
        super("No connection: " + message);
    }
}
