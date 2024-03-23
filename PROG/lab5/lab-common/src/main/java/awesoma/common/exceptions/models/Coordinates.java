package awesoma.common.models;

import awesoma.common.exceptions.ValidationException;

/**
 * Class realises coordinates representation
 */
public class Coordinates {
    private static final long maxY = 117;
    private double x;
    private Long y; // notNull <117

//    public Coordinates() {
//    }

    public Coordinates(double x, long y) throws ValidationException {
        this.x = x;
        if (y >= maxY) {
            throw new ValidationException("Y must be 117");
        }
        this.y = y;
    }

    public long getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
