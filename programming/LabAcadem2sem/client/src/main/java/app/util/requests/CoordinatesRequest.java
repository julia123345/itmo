package app.util.requests;

import app.model.Coordinates;
import app.util.Console;

import java.io.IOException;

/** Coordinates: x (Long, max 899), y (Integer). */
public class CoordinatesRequest extends Request {

    public CoordinatesRequest(Console console) {
        super(console);
    }

    @Override
    public Coordinates create() {
        try {
            console.println("Введите координату x (Long, макс. 899): ");
            long x = getValidLong(v -> v <= 899, "Значение x должно быть не больше 899. Повторите ввод: ");
            console.println("Введите координату y (Integer): ");
            int y = getValidInt();
            return new Coordinates(x, y);
        } catch (IOException e) {
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}
