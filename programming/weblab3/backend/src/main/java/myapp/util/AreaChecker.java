package myapp.util;

public class AreaChecker {

    public static boolean check(double x, double y, double r) {
        // I четверть (x >= 0, y >= 0) - четверть круга радиусом r
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= r * r;
        }

        // II четверть (x <= 0, y >= 0) - треугольник (ширина r, высота r/2)
        if (x <= 0 && y >= 0) {
            return x >= -r && y <= r/2 && y <= (r/2) * (1 + x/r);
        }

        // III четверть (x <= 0, y <= 0) - прямоугольник (ширина r, высота r/2)
        if (x <= 0 && y <= 0) {
            return x >= -r && y >= -r/2;
        }
        return false;
    }
}