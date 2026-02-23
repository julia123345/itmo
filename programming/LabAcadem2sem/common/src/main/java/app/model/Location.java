package app.model;

import java.io.Serializable;
import java.util.Objects;

/** Location: x, y, z (Long), name (String, max 782, nullable) */
public class Location implements Serializable, Cloneable, Comparable<Location> {

    private Long x;   // Поле не может быть null
    private Long y;   // Поле не может быть null
    private Long z;   // Поле не может быть null
    private String name; // Длина строки не больше 782, Поле может быть null

    public Location(Long x, Long y, Long z, String name) {
        setX(x);
        setY(y);
        setZ(z);
        setName(name);
    }

    public Location() {}

    public Long getX() { return x; }
    public void setX(Long x) {
        if (x == null) throw new IllegalArgumentException("Location.x не может быть null");
        this.x = x;
    }
    public Long getY() { return y; }
    public void setY(Long y) {
        if (y == null) throw new IllegalArgumentException("Location.y не может быть null");
        this.y = y;
    }
    public Long getZ() { return z; }
    public void setZ(Long z) {
        if (z == null) throw new IllegalArgumentException("Location.z не может быть null");
        this.z = z;
    }
    public String getName() { return name; }
    public void setName(String name) {
        if (name != null && name.length() > 782) name = name.substring(0, 782);
        this.name = name;
    }

    @Override
    public Location clone() {
        return new Location(x, y, z, name);
    }

    @Override
    public String toString() {
        return "Location{x=" + x + ", y=" + y + ", z=" + z + ", name='" + name + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(x, location.x) && Objects.equals(y, location.y)
                && Objects.equals(z, location.z) && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, name);
    }

    @Override
    public int compareTo(Location o) {
        int c = Long.compare(this.x, o.x);
        if (c != 0) return c;
        c = Long.compare(this.y, o.y);
        if (c != 0) return c;
        c = Long.compare(this.z, o.z);
        if (c != 0) return c;
        return Objects.compare(this.name, o.name, String::compareTo);
    }
}