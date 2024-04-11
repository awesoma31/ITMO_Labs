package org.awesoma.server;

import org.awesoma.common.Environment;

public class App {
    public static void main(String[] args) {
        new TCPServer(Environment.HOST, Environment.PORT).run();
    }
}

