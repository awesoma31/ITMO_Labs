package org.awesoma.server;

import org.awesoma.common.exceptions.ValidationException;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, ValidationException {
        new Server("localhost", 8000).run();
    }
}

