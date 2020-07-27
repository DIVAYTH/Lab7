package collectionClasses;

import java.io.Serializable;

public class Location implements Serializable {
    private double x;
    private int y;
    private Integer z; //Поле не может быть null

    public Location(double x, int y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "(" +
                "x = " + x +
                ", y = " + y +
                ", z = " + z +
                ')';
    }
}