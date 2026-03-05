package app.commands;

import app.User;
import app.model.*;
import app.server.Server;
import app.managers.CollectionManager;
import app.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;
import java.time.ZonedDateTime;
import java.util.Random;

public class GenerateRandomCommand extends ServerCommand {
    private final CollectionManager collectionManager;
    private final Random random = new Random();

    // Константы для генерации
    private static final String[] NAMES = {
            "Ivan", "Maria", "Petr", "Elena", "Dmitry",
            "Olga", "Michael", "Anna", "Vladimir", "Tatiana", "Nikolai"
    };

    private static final EyeColor[] EYE_COLORS = EyeColor.values();
    private static final HairColor[] HAIR_COLORS = HairColor.values();
    private static final Country[] COUNTRIES = Country.values();

    public GenerateRandomCommand(Server server, CollectionManager collectionManager) {
        super(server, "generate_random", "Добавить случайного человека в коллекцию", CommandType.GENERATE_RANDOM);
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        try {
            if (user == null) {
                getServer().sendError(key, "Необходимо авторизоваться");
                return false;
            }

            Person randomPerson = generateRandomPerson(user.getLogin());
            collectionManager.add(randomPerson);

            getServer().sendOK(key, "Случайный человек успешно добавлен! ID: " + randomPerson.getId() +
                    ", имя: " + randomPerson.getName());

            return true;

        } catch (Exception e) {
            getServer().sendError(key, "Ошибка при генерации: " + e.getMessage());
            return false;
        }
    }

    private Person generateRandomPerson(String ownerLogin) {
        Person person = new Person();

        person.setName(generateRandomName());
        person.setOwnerLogin(ownerLogin);
        person.setCreationDate(ZonedDateTime.now());
        person.setCoordinates(generateRandomCoordinates());
        person.setHeight(generateRandomHeight());
        person.setEyeColor(generateRandomEyeColor());
        person.setHairColor(generateRandomHairColor());

        if (random.nextInt(100) < 50) {
            person.setNationality(generateRandomCountry());
        } else {
            person.setNationality(null);
        }

        person.setLocation(generateRandomLocation());

        return person;
    }

    private String generateRandomName() {
        return NAMES[random.nextInt(NAMES.length)] + "_" + random.nextInt(10000);
    }

    private Coordinates generateRandomCoordinates() {
        Long x = 1L + (long)(Math.random() * 899);
        Integer y = 1 + (int)(Math.random() * 1000);
        return new Coordinates(x, y);
    }

    private Long generateRandomHeight() {
        return 50L + (long)(Math.random() * 200);
    }

    private EyeColor generateRandomEyeColor() {
        return EYE_COLORS[random.nextInt(EYE_COLORS.length)];
    }

    private HairColor generateRandomHairColor() {
        return HAIR_COLORS[random.nextInt(HAIR_COLORS.length)];
    }

    private Country generateRandomCountry() {
        return COUNTRIES[random.nextInt(COUNTRIES.length)];
    }

    private Location generateRandomLocation() {
        Long x = 1L + (long)(Math.random() * 999);
        Long y = 1L + (long)(Math.random() * 999);
        Long z = 1L + (long)(Math.random() * 999);

        String name = null;
        if (Math.random() < 0.8) {
            String[] locationNames = {"Home", "Work", "School", "Park", "Mall", "Downtown", "Airport", "Station"};
            name = locationNames[random.nextInt(locationNames.length)] + "_" + random.nextInt(100);
        }

        return new Location(x, y, z, name);
    }
}