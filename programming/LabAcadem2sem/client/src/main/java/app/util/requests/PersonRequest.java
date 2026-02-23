package app.util.requests;

import app.model.*;
import ru.bright.model.*;
import app.util.Console;

import java.io.IOException;

/**
 * Интерактивный ввод Person (по одному полю в строку, приглашение с именем поля).
 * id и creationDate генерируются на сервере.
 */
public class PersonRequest extends Request {

    public PersonRequest(Console console) {
        super(console);
    }

    @Override
    public Person create() {
        try {
            console.println("Введите имя (name): ");
            String name = getValidString();
            console.println("Введите координаты (coordinates): ");
            Coordinates coordinates = new CoordinatesRequest(console).create();
            if (coordinates == null) return null;
            console.println("Введите рост (height, Long > 0): ");
            long h = getValidLong(v -> v > 0, "Значение должно быть больше 0. Повторите ввод: ");
            Long height = h;
            console.println("Введите цвет глаз (eyeColor): ");
            EyeColor eyeColor = getValidEnum(EyeColor.class);
            console.println("Введите цвет волос (hairColor): ");
            HairColor hairColor = getValidEnum(HairColor.class);
            console.println("Введите национальность (nationality), пустая строка = null: ");
            Country nationality = getOptionalEnum(Country.class);
            console.println("Введите локацию (location): ");
            Location location = new LocationRequest(console).create();
            if (location == null) return null;
            return new Person(name, coordinates, height, eyeColor, hairColor, nationality, location);
        } catch (IOException e) {
            console.printErr("Ошибка чтения");
            return null;
        }
    }
}