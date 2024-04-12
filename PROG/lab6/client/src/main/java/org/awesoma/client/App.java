package org.awesoma.client;

import org.awesoma.common.Environment;

public class App {
    public static void main(String[] args) {
        new Client(Environment.HOST, Environment.PORT).run();
    }
}
