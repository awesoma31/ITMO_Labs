package org.awesoma.server;

import org.awesoma.common.Environment;
import org.awesoma.common.util.CLIArgumentParser;

public class App {
    public static void main(String[] args) {
        CLIArgumentParser.parseArgs(args);

        new TCPServer(Environment.HOST, Environment.PORT).run();
    }
}

