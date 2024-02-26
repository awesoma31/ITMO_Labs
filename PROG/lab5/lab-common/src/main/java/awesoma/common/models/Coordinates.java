package awesoma.common.models;

/**
 * Class realises coordinates representation
 */
public class Coordinates {
    public static long maxY = 117;
    private double x;
    private Long y; // notNull <117

    public Coordinates() {
    }

    public Coordinates(double x, long y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
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
