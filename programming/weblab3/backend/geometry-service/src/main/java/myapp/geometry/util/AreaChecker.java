package myapp.geometry.util;

public class AreaChecker {

    public static boolean check(double x, double y, double r) {
        // I quadrant (x >= 0, y >= 0) - quarter circle radius r
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= (r * r)/2;
        }

        // II quadrant (x <= 0, y >= 0) - triangle (width r, height r/2)
        if (x <= 0 && y >= 0) {
            return x >= -r && y <= r / 2 && y <= (r / 2) * (1 + x / r);
        }

        // III quadrant (x <= 0, y <= 0) - rectangle (width r, height r/2)
        if (x <= 0 && y <= 0) {
            return x >= -r && y >= -r / 2;
        }
        return false;
    }
}

