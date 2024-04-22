package org.awesoma.server.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
    private static String HELIOS_DB_URL = "jdbc:postgresql://pg:5432/studs";
    private static String HELIOS_DB_USERNAME;
    private static String HELIOS_DB_PASS;
    private static String HELIOS_DB_PORT;
    private static String HELIOS_DB_HOST;
    private static String HELIOS_DB_NAME;
    private final Properties info;
    private Connection connection;

    public DBManager() {
        info = new Properties();
        try {
            info.load(new FileInputStream("db.cfg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getInfo() {
        return info;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(HELIOS_DB_URL, info);
    }



//    private static void getDBProperties() {
//        Properties properties = new Properties();
//        try (FileInputStream fis = new FileInputStream("db.cfg")) {
//            properties.load(fis);
//            HELIOS_DB_HOST = properties.getProperty("db.host");
//            HELIOS_DB_PORT = properties.getProperty("db.port");
//            HELIOS_DB_USERNAME = properties.getProperty("db.username");
//            HELIOS_DB_PASS = properties.getProperty("db.password");
//            HELIOS_DB_NAME = properties.getProperty("db.name");
//            HELIOS_DB_URL = "jdbc:postgresql://" + HELIOS_DB_HOST + ":" + HELIOS_DB_PORT + "/" + HELIOS_DB_USERNAME;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}