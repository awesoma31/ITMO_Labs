package org.awesoma.server;

import org.awesoma.common.Environment;

import java.net.BindException;

public class App {
    public static void main(String[] args) {
        try {
            new TCPServer(Environment.HOST, Environment.PORT).run();
        } catch (BindException e) {
            System.err.println("Error binding server: " + e.getMessage());
            System.exit(1);
        }
    }
}

