package flowercity.world;

import flowercity.world.characters.Button;
import flowercity.world.enums.Landscape;
import flowercity.world.exceptions.IllegalStateWorldException;
import flowercity.world.exceptions.TravelException;
import flowercity.world.exceptions.WorldException;
import flowercity.world.locations.Bridge;
import flowercity.world.locations.City;
import flowercity.world.locations.Hill;
import flowercity.world.locations.Road;
import flowercity.world.locations.River;
import flowercity.world.records.PathDescription;
import flowercity.world.vehicles.Steamboat;

import java.util.Random;

/**
 * Точка входа в приложение с небольшим сценарием
 * путешествия Кнопочки по Цветочному городу.
 */
public class Main {

    /**
     * Запускает сценарий, создаёт мир, персонажей и отрабатывает
     * проверяемые и непроверяемые исключения согласно ТЗ.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Random random = new Random();

        Button button = new Button("Кнопочка", random.nextBoolean());
        button.setLogger(System.out::println);

        Road mainRoad = new Road("Главная дорога", "ведущая вдаль", true,
                Landscape.values()[random.nextInt(Landscape.values().length)]);
        River river = new River("Речка", "шумная и прозрачная", 2.5);
        Bridge bridge = new Bridge("Старый мост", "перекинут через реку", 120);
        Hill hill = new Hill("Высокий холм", "с которого виден весь город", 50);
        City city = new City("Цветочный город", "уютный и солнечный", 10_000);

        Steamboat ship = new Steamboat("Смелый", true, 20);

        PathDescription path = new PathDescription(mainRoad.getName(), city.getName(), 3.5);
        System.out.println(path.getInfo());

        try {
            button.moveTo(mainRoad);

            if (random.nextDouble() < 0.15) {
                throw new flowercity.world.exceptions.LocationInaccessibleException(
                        "Путь к мосту \"" + bridge.getName() + "\" временно перекрыт.");
            }

            button.moveTo(bridge);
            button.moveTo(river);
            button.moveTo(hill);
            button.moveTo(city);

            ship.setProducingSmoke(true);
            ship.consumeFuel();
            ship.consumeFuel();

            if (ship.getFuelLevel() <= 0) {
                throw new IllegalStateWorldException("Пароход " + ship.getName() + " не может отплыть: закончилось топливо.");
            }

            ship.accept(button);

            validateWorld(button, ship);

            Button anotherButton = new Button("Кнопочка", button.hasSeenRealSteamboat());
            System.out.println("Проверка equals для персонажа: " + button.equals(anotherButton));
            System.out.println("Строковое представление парохода: " + ship);
            System.out.println("Строковое представление локации: " + mainRoad);

        } catch (TravelException e) {
            System.err.println(e.getMessage());
        } catch (WorldException e) {
            System.err.println(e.getMessage());
        } catch (IllegalStateWorldException e) {
            System.err.println("НЕПРОВЕРЯЕМОЕ ИСКЛЮЧЕНИЕ (ТЗ): " + e.getMessage());
        } finally {
            System.out.println("Текущее настроение Кнопочки: " + button.getMoodLevel());
        }
    }

    private static void validateWorld(Button button, Steamboat ship) throws WorldException {
        if (button.getMoodLevel() < 0) {
            throw new WorldException("Мир стал слишком мрачным для " + button.getName() + ".");
        }
        if (ship.getPollutionLevel() > 5.0) {
            throw new WorldException("Воздух слишком загрязнён из-за парохода " + ship.getName() + ".");
        }
    }
}