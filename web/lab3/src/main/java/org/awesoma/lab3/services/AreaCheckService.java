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
        if (y > 0 && x < 0) {
            return false;
        }
        if (y < 0 && x < 0) {
            if ((y*y + x*x) > r*r) {
                return false;
            }
        }
        if (y > 0 && x > 0) {
            if (y > (((double) -1/2)*x) + r/2) {
                return false;
            }
        }
        if (x > 0 && y < 0) {
            return !(x > r) && !(y < -r / 2);
        }
        return true;
    }
}
