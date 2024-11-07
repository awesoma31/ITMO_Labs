package org.awesoma.back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name="points")
public class Point implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private long id;

    @Column(name="x", nullable=false)
    private double x;

    @Column(name="y", nullable=false)
    private double y;

    @Column(name="r", nullable=false)
    private double r;

    @Column(name="result", nullable=false)
    private boolean result;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User owner;

    public Point(double x, double y, double r, boolean result) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
    }
}