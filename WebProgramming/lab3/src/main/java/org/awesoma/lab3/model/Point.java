package org.awesoma.lab3.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="points")
public record Point(
        @Column(name="x") double x,
        @Column(name="y") double y,
        @Column(name="r") double r,
        @Column(name="creation_time") LocalDateTime creationTime,
        @Column(name="execution_time") long execTime,
        @Column(name="result") boolean result
) implements Serializable {
    public Point(double x, double y, double r, long execTime, boolean isInside) {
        this(x, y, r, LocalDateTime.now(), execTime, isInside);
    }

    public Point(double r, double y, double x) {
        this(x, y, r, null, 0, false);
    }
}
