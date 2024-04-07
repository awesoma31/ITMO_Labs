package org.awesoma.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.awesoma.common.Environment;
import org.awesoma.common.exceptions.ValidationException;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, ValidationException {
        new Server(Environment.HOST, Environment.PORT).run();
    }
}

