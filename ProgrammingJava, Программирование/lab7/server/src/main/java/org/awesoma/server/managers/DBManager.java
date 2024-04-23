package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.UserCredentials;
import org.awesoma.common.models.Coordinates;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.Person;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
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

    public void addMovie(Movie m, UserCredentials user) throws SQLException {
        var owner_id = getOwnerByUsername(user.username());
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
                "operator_id, " +
                "owner_id" + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, m.getName());
        ps.setInt(2, c_id);
        ps.setTimestamp(3, Timestamp.valueOf(m.getCreationDate()));
        ps.setInt(4, m.getOscarsCount());
        ps.setInt(5, m.getTotalBoxOffice());
        ps.setLong(6, m.getUsaBoxOffice());
        ps.setString(7, m.getGenre() == null ? null : m.getGenre().name());
        ps.setInt(8, op_id);
        ps.setInt(9, owner_id);
        ps.execute();
    }

    private int addPerson(Person p) throws SQLException {
        var query = "INSERT INTO person(name, birthday, weight, eye_color, nationality) VALUES (?, ?, ?, ?, ?) returning id";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, p.getName());
        ps.setTimestamp(2, Timestamp.valueOf(p.getBirthday()));
        ps.setDouble(3, p.getWeight());
        ps.setString(4, (p.getEyeColor() != null ? p.getEyeColor().name() : null));
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

    public void clear(String username) throws SQLException {
        var u_id = getOwnerByUsername(username);
        var query = "delete from movie where owner_id = ?";
        var ps = connection.prepareStatement(query);
        ps.setInt(1, u_id);
        ps.execute();
    }

    private int getOwnerByUsername(String username) throws SQLException {
        var query = "SELECT id from users where username = ?";
        var ps = connection.prepareStatement(query);
        ps.setString(1, username);
        var res = ps.executeQuery();
        if (res.next()) {
            return res.getInt("id");
        } else {
            throw new SQLException("User with such username not found");
        }


    }

    public void authenticateUser(UserCredentials user) throws SQLException {
        if (isUserExists(user.username())) {
            // Проверяем правильность пароля
            if (!isPasswordCorrect(user)) {
                throw new SQLException("Wrong password");
            }
//            return isPasswordCorrect(username, password);
        } else {
            // Если пользователя нет, добавляем его в таблицу users
            addUser(user);
//            return true; // Пользователь успешно добавлен
        }
    }

    private boolean isUserExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    private boolean isPasswordCorrect(UserCredentials user) throws SQLException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.username());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(user.password());
                }
            }
        }
        return false;
    }

    public void addUser(UserCredentials user) throws SQLException {
        if (!isUserExists(user.username())) {

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                var res = ps.executeUpdate();
                if (res != 0) {
                    logger.info("User <" + user.username() + "> added successfully");
                }
            }
        }
    }

    public void checkUser(UserCredentials userCredentials) throws SQLException {
        try {
            authenticateUser(userCredentials);
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
//
//        if (!isUserRegistered(username)) {
//            addUser(userCredentials);
//        } else if (!checkUserPassword(userCredentials)){
//            throw new SQLException("Login or password authentication failed");
//        }

    }

    private boolean checkUserPassword(UserCredentials userCredentials) throws SQLException {
        var u_pswd = userCredentials.password();
        var q = "SELECT password from users where username = ?";
        var ps = connection.prepareStatement(q);
        ps.setString(1, u_pswd);
        var res = ps.executeQuery();
        if (res.next()) {
            if (!Objects.equals(res.getString("password"), u_pswd)) {
                return false;
            }
            return true;
        }
        throw new SQLException("User not found");
    }

//    public void addUser(UserCredentials userCredentials) throws SQLException {
//        var q = "insert into users(username, password) values (?, ?)";
//        var ps = connection.prepareStatement(q);
//        ps.setString(1, userCredentials.username());
//        ps.setString(2, userCredentials.password());
//    }

    private boolean isUserRegistered(String username) throws SQLException {
//        var q = "SELECT count(username) from users where username = ?";
//        var ps = connection.prepareStatement(q);
//        ps.setString(1, username);
//        var res = ps.executeQuery();
//        if (res.next()) {
//            if (res.getInt(1) > 0) {
//                return true;
//            }
//            return false;
//        } else {
//            return false;
//        }
        String query = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
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