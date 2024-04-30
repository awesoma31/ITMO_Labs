package org.awesoma.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.common.Environment;
import org.awesoma.common.UserCredentials;
import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

public class DBManager {
    private final Logger logger = LogManager.getLogger(DBManager.class);
    private Properties info;
    private final String dbURL = Environment.getDbUrl();
    private Connection connection;

    public DBManager() throws IOException, SQLException {
        loadConfigurationInfo();
        connect();
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(dbURL, info);
        } catch (SQLException e) {
            try {
                var info = new Properties();
                info.load(new FileInputStream(Environment.getDefaultDbConfigFilePath()));
                connection = DriverManager.getConnection(dbURL, info);
            } catch (IOException | SQLException ex) {
                logger.error("Connecting to DB failed: " + e.getMessage());
                System.exit(1);
            }

        }
        logger.info("Connection with DB sustained successfully");
    }

    private void loadConfigurationInfo() throws IOException {
        info = new Properties();
        try {
            info.load(new FileInputStream(Environment.getDbConfigFilePath()));
        } catch (IOException e) {
            info.load(new FileInputStream(Environment.getDefaultDbConfigFilePath()));
        }
    }

    public void addMovie(Movie m, UserCredentials user) throws SQLException {
        var owner_id = getOwnerIdByUsername(user.username());
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
        var u_id = getOwnerIdByUsername(username);
        var query = "delete from movie where owner_id = ?";
        var ps = connection.prepareStatement(query);
        ps.setInt(1, u_id);
        ps.execute();
    }

    private int getOwnerIdByUsername(String username) throws SQLException {
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
            if (!isPasswordCorrect(user)) {
                throw new SQLException("Wrong password");
            }
        } else {
            addUser(user);
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

    public Vector<Movie> readCollection() throws SQLException {
        var col = new Vector<Movie>();
        var q = "select * from movie";
        var ps = connection.prepareStatement(q);
        var res = ps.executeQuery();
        while (res.next()) {
            var movie = getMovie(res);
            col.add(movie);
        }
        return col;
    }

    private Movie getMovie(ResultSet res) throws SQLException {
        var genre = res.getString("genre") != null ? MovieGenre.valueOf(res.getString("genre")) : null;
        var operator = getOperator(res.getInt("operator_id"));
        try {
            return new Movie(
                    res.getInt("id"),
                    res.getString("name"),
                    res.getInt("oscarsCount"),
                    res.getInt("totalBoxOffice"),
                    res.getLong("totalBoxOffice"),
                    getCoordinates(res.getInt("coordinates_id")),
                    res.getTimestamp("creationDate").toLocalDateTime(),
                    genre,
                    operator
            );
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private Person getOperator(int operatorId) throws SQLException {
        var q = "select * from person where id = ?";
        var ps = connection.prepareStatement(q);
        ps.setInt(1, operatorId);
        var res = ps.executeQuery();
        if (res.next()) {
            try {
                var color = res.getString("eye_color") != null ? Color.valueOf(res.getString("eye_color")) : null;
                var country = res.getString("nationality") != null ? Country.valueOf(res.getString("nationality")) : null;
                return new Person(
                        res.getString("name"),
                        res.getTimestamp("birthday").toLocalDateTime(),
                        res.getFloat("weight"),
                        color,
                        country
                );
            } catch (ValidationException e) {
                throw new CommandExecutingException(e.getMessage());
            }
        }
        throw new CommandExecutingException("operator not found");
    }

    private Coordinates getCoordinates(int id) throws SQLException {
        var q = "select * from coordinates where id = ?";
        var ps = connection.prepareStatement(q);
        ps.setInt(1, id);
        var res = ps.executeQuery();
        if (res.next()) {
            try {
                return new Coordinates(res.getDouble("x"), res.getLong("y"));
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
        throw new CommandExecutingException("Coordinates not found");
    }

    public void removeById(int id, UserCredentials user) {
        try {
            authenticateUser(user);
            var q = "delete from movie where id = ? and owner_id = ?";
            var ps = connection.prepareStatement(q);
            ps.setInt(1, id);
            ps.setInt(2, getOwnerIdByUsername(user.username()));
            if (ps.executeUpdate() == 0) {
                throw new CommandExecutingException("Element with such id and owner wasn't found");
            }
        } catch (SQLException e) {
            throw new CommandExecutingException(e.getMessage());
        }
    }

    public void updateElementById(int id, Movie m, UserCredentials user) {
        try {
            updateCoordinatesById(id, m.getCoordinates(), user);
            updatePersonById(id, m.getOperator(), user);

            var q = "update movie " +
                    "set name = ?, " +
                    "creationdate = ?, " +
                    "oscarscount = ?, " +
                    "totalboxoffice = ?, " +
                    "usaboxoffice = ?, " +
                    "genre = ? " +
                    "where id = ? and owner_id = ?";
            var ps = connection.prepareStatement(q);
            var genre = m.getGenre() != null ? m.getGenre().name() : null;
            ps.setString(1, m.getName());
            ps.setTimestamp(2, Timestamp.valueOf(m.getCreationDate()));
            ps.setInt(3, m.getOscarsCount());
            ps.setInt(4, m.getTotalBoxOffice());
            ps.setLong(5, m.getUsaBoxOffice());
            ps.setString(6, genre);
            ps.setInt(7, id);
            ps.setInt(8, getOwnerIdByUsername(user.username()));
            ps.execute();
            logger.info("Element updated successfully");
        } catch (SQLException e) {
            throw new CommandExecutingException(e.getMessage());
        }
    }

    private void updatePersonById(int id, Person p, UserCredentials user) throws SQLException {
        var color = p.getEyeColor() != null ? p.getEyeColor().name() : null;
        var country = p.getNationality() != null ? p.getNationality().name() : null;
        authenticateUser(user);
        var q = "update person " +
                "set name = ?, " +
                "birthday = ?, " +
                "weight = ?, " +
                "eye_color = ?, " +
                "nationality = ? " +
                "where id = ? and owner_id = ?";
        var ps = connection.prepareStatement(q);
        ps.setString(1, p.getName());
        ps.setTimestamp(2, Timestamp.valueOf(p.getBirthday()));
        ps.setDouble(3, p.getWeight());
        ps.setString(4, color);
        ps.setString(5, country);
        ps.setInt(6, id);
        ps.setInt(7, getOwnerIdByUsername(user.username()));
        ps.execute();
    }

    private void updateCoordinatesById(int id, Coordinates c, UserCredentials user) throws SQLException {
        var q = "update coordinates " +
                "set x = ?, " +
                "y = ? " +
                "where id = ? and owner_id = ?";
        var ps = connection.prepareStatement(q);
        ps.setDouble(1, c.getX());
        ps.setLong(2, c.getY());
        ps.setInt(3, id);
        ps.setInt(4, getOwnerIdByUsername(user.username()));
        ps.execute();
    }
}