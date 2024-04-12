package org.awesoma.server;

import org.awesoma.common.Environment;

import java.sql.*;

public class App {
    //    public static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
//    public static final String DB_PASSWD = "yONE%4288";
//    public static final String USER = "pg";


    public static void main(String[] args) {
        new TCPServer(Environment.HOST, Environment.PORT).run();
    }
}

