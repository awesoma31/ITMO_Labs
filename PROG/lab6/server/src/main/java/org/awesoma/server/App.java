package org.awesoma.server;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Server("localhost", 8000).run();
    }
}

