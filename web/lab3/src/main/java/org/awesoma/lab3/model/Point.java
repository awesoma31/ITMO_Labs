package org.awesoma.lab3.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record Point(
        double x,
        double y,
        double r,
        LocalDateTime creationTime,
        long executionTime,
        boolean result
) implements Serializable {
    public Point(double x, double y, double r, long executionTime, boolean result) {
        this(x, y, r, LocalDateTime.now(), executionTime, result);
    }

    public Point(double r, double y, double x) {
        this(x, y, r, null, 0, false);
    }
}
