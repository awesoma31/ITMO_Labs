package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.models.Coordinates;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBManager {
    private final Logger logger = LogManager.getLogger(DBManager.class);
    private static String HELIOS_DB_URL = "jdbc:postgresql://pg:5432/studs";
    private static String DB_URL = "jdbc:postgresql://localhost:5432/lab7";
    private static String HELIOS_DB_USERNAME;
    private static String HELIOS_DB_PASS;
    private static String HELIOS_DB_PORT;
    private static String HELIOS_DB_HOST;
    private static String HELIOS_DB_NAME;
    private final Properties info;
    private Connection connection;

    public DBManager() throws IOException, SQLException {
        info = new Properties();
        info.load(new FileInputStream("db.cfg"));
        logger.info("Properties loaded successfully");
        connect();
    }

    public Properties getInfo() {
        return info;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, info);
        logger.info("Connection with DB sustained successfully");
    }

    public void addMovie(Movie m) throws SQLException {
        var c_id = addCoordinates(m.getCoordinates());
        var op_id = addPerson(m.getOperator());
        var query = "INSERT INTO movie(" +
                "name, " +
                "coordinates_id, " +
                "creationdate, " +
                "oscarscount, " +
                "totalboxoffice, " +
                "usaboxoffice, " +
                "genre, " +
                "operator_id" +
                ") values (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, m.getName());
        ps.setInt(2, c_id);
        ps.setTimestamp(3, Timestamp.valueOf(m.getCreationDate()));
        ps.setInt(4, m.getOscarsCount());
        ps.setInt(5, m.getTotalBoxOffice());
        ps.setLong(6, m.getUsaBoxOffice());
        ps.setString(7, m.getGenre().name());
        ps.setInt(8, op_id);
        ps.execute();
    }

    private int addPerson(Person p) throws SQLException{
        var query = "INSERT INTO person(name, birthday, weight, eye_color, nationality) VALUES (?, ?, ?, ?, ?) returning id";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, p.getName());
        ps.setTimestamp(2, Timestamp.valueOf(p.getBirthday()));
        ps.setDouble(3, p.getWeight());
        ps.setString(4, p.getEyeColor().name());
        ps.setString(5, p.getNationality().name());
        var res = ps.executeQuery();
        res.next();
        return res.getInt("id");
    }

    public int addCoordinates(Coordinates c) throws SQLException {
        var query = "INSERT INTO coordinates(x, y) VALUES (?, ?) returning id";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setFloat(1, (float) c.getX());
        ps.setLong(2, c.getY());
        var res = ps.executeQuery();
        res.next();
        return res.getInt("id");
    }

    public void clear() throws SQLException {
        var query = "TRUNCATE table movie";
        var ps = connection.prepareStatement(query);
        ps.execute();
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