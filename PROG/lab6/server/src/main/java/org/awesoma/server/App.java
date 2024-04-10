package org.awesoma.server;

import org.awesoma.common.Environment;
import org.awesoma.common.exceptions.ValidationException;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        new Server(Environment.HOST, Environment.PORT).run();
    }
}

