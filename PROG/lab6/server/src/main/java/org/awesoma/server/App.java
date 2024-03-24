package org.awesoma.server;

import org.awesoma.common.models.*;
import org.awesoma.common.util.UniqueIdGenerator;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.DumpManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class App {
    public static final int PORT = 1821;
    public static final int CONNECTION_TIMEOUT = 0;
    public static final String ENV = "lab6";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Server server = new Server(PORT);
        server.run();
    }
}

