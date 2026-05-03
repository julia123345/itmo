package serverapp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;

public class PointResult implements Serializable {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String time;

    public PointResult(double x, double y, double r, boolean hit, long timestamp) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Геттеры
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
    public String getTime() { return time; }

    // Сеттеры
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setR(double r) { this.r = r; }
    public void setHit(boolean hit) { this.hit = hit; }
    public void setTime(String time) { this.time = time; }
}