package app.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Coordinates: x (Long, max 899), y (Integer), оба не null
 */
public class Coordinates implements Cloneable, Comparable<Coordinates>, Serializable {
    private Long x;   // Максимальное значение поля: 899, Поле не может быть null
    private Integer y; // Поле не может быть null

    public Coordinates(Long x, Integer y) {
        setX(x);
        setY(y);
    }

    public Coordinates() {}

    public Long getX() { return x; }
    public void setX(Long x) {
        if (x == null) throw new IllegalArgumentException("Coordinates.x не может быть null");
        if (x > 899) throw new IllegalArgumentException("Coordinates.x не может быть больше 899");
        this.x = x;
    }
    public Integer getY() { return y; }
    public void setY(Integer y) {
        if (y == null) throw new IllegalArgumentException("Coordinates.y не может быть null");
        this.y = y;
    }

    @Override
    public Coordinates clone() {
        return new Coordinates(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Coordinates o) {
        int c = Long.compare(this.x, o.x);
        if (c != 0) return c;
        return Integer.compare(this.y, o.y);
    }
}