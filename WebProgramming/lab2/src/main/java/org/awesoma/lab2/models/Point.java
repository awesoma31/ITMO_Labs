package org.awesoma.lab2.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record Point(double x, double y, double r, LocalDateTime now, long execTime,  boolean isInside) {
//    public Point(double x, double y, double r, long execTime) {
//        this(x, y, r, LocalDateTime.now(), execTime, isInside(x, y, r));
//    }

    public Point(double x, double y, double r, long execTime, boolean isInside) {
        this(x, y, r, LocalDateTime.now(), execTime, isInside);
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isInside(double x, double y, double r) {
        if (x > 0 && y > 0) {
            return false;
        }

        if (x > 0 && y < 0) {
            if (x * x + y * y > (r * r) / 4) {
                return false;
            }
        }

        if (x < 0 && y < 0) {
            if (x < -r || y < -(r / 2)) {
                return false;
            }
        }

        if (x < 0 && y > 0) {
            if (y - 2 * x > r) {
                return false;
            }
        }

        return true;
    }
}
