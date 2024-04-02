package org.awesoma;

import org.awesoma.common.exceptions.UnrecognisedCommandException;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, UnrecognisedCommandException {
        new Client("localhost", 8000).run();
    }
}
