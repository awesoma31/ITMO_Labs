package org.awesoma.lab3.services;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.awesoma.lab3.model.Point;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Named
@SessionScoped
public class AreaCheckService implements Serializable {

    public Point checkAndGetPoint(Double x, Double y, Double r) {
        var creationTime = LocalDateTime.now();
        var startTime = Instant.now();
        boolean isInside = checkArea(x, y, r);
        var endTime = Instant.now();
        var executionTime = ChronoUnit.NANOS.between(startTime, endTime);

        return new Point(x, y, r, creationTime, executionTime, isInside);
    }

    private boolean checkArea(Double x, Double y, Double r) {
        if (x < -2 || x > 2) {
            return false;
        }
        if (y < -5 || y > 5) {
            return false;
        }
        return r >= 1 && r <= 5;
    }
}
