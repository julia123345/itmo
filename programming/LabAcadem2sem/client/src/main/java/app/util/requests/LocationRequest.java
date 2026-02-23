package app.util.requests;

import app.model.Location;
import app.util.Console;

import java.io.IOException;

/** Location: x, y, z (Long), name (String, max 782, null если пусто). */
public class LocationRequest extends Request {

    public LocationRequest(Console console) {
        super(console);
    }

    @Override
    public Location create() {
        try {
            console.println("Введите координату x локации (Long): ");
            long x = getValidLong();
            console.println("Введите координату y локации (Long): ");
            long y = getValidLong();
            console.println("Введите координату z локации (Long): ");
            long z = getValidLong();
            console.println("Введите имя локации (до 782 символов, пустая строка = null): ");
            String name = getOptionalString();
            if (name != null && name.length() > 782) name = name.substring(0, 782);
            return new Location(x, y, z, name);
        } catch (IOException e) {
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}
