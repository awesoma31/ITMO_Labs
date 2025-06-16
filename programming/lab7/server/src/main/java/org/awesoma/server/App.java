package org.awesoma.server;

import org.awesoma.common.Environment;
import org.awesoma.common.util.CLIArgumentParser;

import static org.awesoma.common.util.CLIArgumentParser.parseArgs;

public class App {
    public static void main(String[] args) {
        parseArgs(args);

        new TCPServer(Environment.HOST, Environment.PORT).run();
    }
}

