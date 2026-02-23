package app.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Person: id (auto), name, coordinates, creationDate (auto), height, eyeColor, hairColor, nationality, location.
 * Модификация/удаление только создателем (ownerLogin).
 */
public class Person implements Cloneable, Comparable<Person>, Serializable {

    private long id;           // > 0, уникальное, генерируется автоматически
    private String ownerLogin; // логин создателя
    private String name;       // не null, не пустая
    private Coordinates coordinates;
    private ZonedDateTime creationDate; // не null, генерируется автоматически
    private Long height;       // не null, > 0
    private EyeColor eyeColor;
    private HairColor hairColor;
    private Country nationality; // может быть null
    private Location location;

    public Person(String name, Coordinates coordinates, Long height,
                  EyeColor eyeColor, HairColor hairColor, Country nationality, Location location) {
        setName(name);
        setCoordinates(coordinates);
        setCreationDate(ZonedDateTime.now());
        setHeight(height);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
        setLocation(location);
    }

    public Person(long id, String ownerLogin, String name, Coordinates coordinates, ZonedDateTime creationDate,
                  Long height, EyeColor eyeColor, HairColor hairColor, Country nationality, Location location) {
        setId(id);
        setOwnerLogin(ownerLogin);
        setName(name);
        setCoordinates(coordinates);
        setCreationDate(creationDate);
        setHeight(height);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
        setLocation(location);
    }

    public Person() {}

    public long getId() { return id; }
    public void setId(long id) {
        if (id <= 0) throw new IllegalArgumentException("id должен быть > 0");
        this.id = id;
    }
    public String getOwnerLogin() { return ownerLogin; }
    public void setOwnerLogin(String ownerLogin) { this.ownerLogin = ownerLogin; }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name не может быть пустым");
        this.name = name;
    }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("coordinates не может быть null");
        this.coordinates = coordinates;
    }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(ZonedDateTime creationDate) {
        if (creationDate == null) throw new IllegalArgumentException("creationDate не может быть null");
        this.creationDate = creationDate;
    }
    public Long getHeight() { return height; }
    public void setHeight(Long height) {
        if (height == null || height <= 0) throw new IllegalArgumentException("height должно быть > 0");
        this.height = height;
    }
    public EyeColor getEyeColor() { return eyeColor; }
    public void setEyeColor(EyeColor eyeColor) {
        if (eyeColor == null) throw new IllegalArgumentException("eyeColor не может быть null");
        this.eyeColor = eyeColor;
    }
    public HairColor getHairColor() { return hairColor; }
    public void setHairColor(HairColor hairColor) {
        if (hairColor == null) throw new IllegalArgumentException("hairColor не может быть null");
        this.hairColor = hairColor;
    }
    public Country getNationality() { return nationality; }
    public void setNationality(Country nationality) { this.nationality = nationality; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) {
        if (location == null) throw new IllegalArgumentException("location не может быть null");
        this.location = location;
    }

    @Override
    public Person clone() {
        Person p = new Person(name, coordinates != null ? coordinates.clone() : null, height,
                eyeColor, hairColor, nationality, location != null ? location.clone() : null);
        try {
            p.setId(id);
        } catch (Exception ignored) {}
        p.setOwnerLogin(ownerLogin);
        p.setCreationDate(creationDate);
        return p;
    }

    @Override
    public String toString() {
        return "Person{ id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + (creationDate != null ? creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null) +
                ", height=" + height +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                ", location=" + location +
                ", owner=" + ownerLogin + " }";
    }

    @Override
    public int compareTo(Person o) {
        int c = name.compareTo(o.name);
        if (c != 0) return c;
        c = Long.compare(id, o.id);
        if (c != 0) return c;
        c = coordinates.compareTo(o.coordinates);
        if (c != 0) return c;
        c = height.compareTo(o.height);
        if (c != 0) return c;
        c = eyeColor.compareTo(o.eyeColor);
        if (c != 0) return c;
        c = hairColor.compareTo(o.hairColor);
        if (c != 0) return c;
        c = Objects.compare(nationality, o.nationality, (a, b) -> a == b ? 0 : (a == null ? -1 : b == null ? 1 : a.compareTo(b)));
        if (c != 0) return c;
        c = location.compareTo(o.location);
        if (c != 0) return c;
        return creationDate.compareTo(o.creationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(name, person.name)
                && Objects.equals(coordinates, person.coordinates)
                && Objects.equals(creationDate, person.creationDate)
                && Objects.equals(height, person.height)
                && eyeColor == person.eyeColor && hairColor == person.hairColor
                && nationality == person.nationality && Objects.equals(location, person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, height, eyeColor, hairColor, nationality, location);
    }
}