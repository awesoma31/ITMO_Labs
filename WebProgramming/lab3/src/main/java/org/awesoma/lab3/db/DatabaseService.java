package org.awesoma.lab3.db;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.awesoma.lab3.model.Point;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

@Named
@SessionScoped
public class DatabaseService implements Serializable {
    private Connection connection;

    private String url;
    private String username;
    private String password;

    public DatabaseService() throws IOException{
        loadConfigurationInfo();
        connect();
    }

    private void loadConfigurationInfo() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.url = prop.getProperty("db.url");
            this.username = prop.getProperty("db.username");
            this.password = prop.getProperty("db.password");
        }
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPoint(Point point) throws SQLException {
        var query = "insert into points(x, y, r, creation_time, execution_time, result) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDouble(1, point.x());
        ps.setDouble(2, point.y());
        ps.setDouble(3, point.r());
        ps.setTimestamp(4, Timestamp.valueOf(point.creationTime()));
        ps.setLong(5, point.executionTime());
        ps.setBoolean(6, point.result());
        ps.execute();
    }

    public ArrayList<Point> getAllPoints() throws SQLException {
        var points = new ArrayList<Point>();

        var query = "select * from points";
        PreparedStatement ps = connection.prepareStatement(query);
        var rs = ps.executeQuery();

        while (rs.next()) {
            var point = new Point(
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("r"),
                    rs.getTimestamp("creation_time").toLocalDateTime(),
                    rs.getLong("execution_time"),
                    rs.getBoolean("result")
            );
            points.add(point);
        }

        return points;
    }
}
