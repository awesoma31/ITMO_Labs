package org.awesoma.lab2.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record Point(double x, double y, double r, LocalDateTime now, long execTime, boolean isInside) implements Serializable {
    public Point(double x, double y, double r, long execTime, boolean isInside) {
        this(x, y, r, LocalDateTime.now(), execTime, isInside);
    }
}
