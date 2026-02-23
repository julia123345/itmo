package flowercity.world.characters;

import flowercity.world.interfaces.WorldLogger;
import flowercity.world.locations.FlowerCityLocation;

import java.util.Objects;

/**
 * Базовый персонаж мира Цветочного города.
 * Хранит имя, уровень настроения и умеет логировать события.
 */
public abstract class Character {
    private final String name;
    private WorldLogger logger;
    private int moodLevel = 50;

    /**
     * Создаёт персонажа с указанным именем.
     *
     * @param name имя персонажа
     */
    public Character(String name) {
        this.name = name;
    }

    /**
     * Устанавливает реализацию логгера для вывода сообщений.
     *
     * @param logger логгер мира
     */
    public void setLogger(WorldLogger logger) {
        this.logger = logger;
    }

    /**
     * Логирует сообщение, если установлен логгер.
     *
     * @param message текст сообщения
     */
    protected void log(String message) {
        if (logger != null) {
            logger.log(message);
        }
    }

    /**
     * @return имя персонажа
     */
    public String getName() {
        return name;
    }

    /**
     * @return текущий уровень настроения
     */
    public int getMoodLevel() {
        return moodLevel;
    }

    /**
     * Меняет настроение на указанное значение.
     *
     * @param delta изменение настроения
     */
    public void adjustMood(int delta) {
        this.moodLevel += delta;
    }

    /**
     * Глагол, который используется при описании прибытия персонажа.
     *
     * @return глагол в прошедшем времени
     */
    protected abstract String getArrivalVerb();

    /**
     * Логирует прибытие в локацию и изменяет настроение
     * в зависимости от погоды.
     *
     * @param location локация прибытия
     */
    protected void logArrival(FlowerCityLocation location) {
        log(String.format("%s %s в локацию: %s. Погода там %s.",
                name, getArrivalVerb(), location.getName(), location.getWeather().getDescription()));
        adjustMood(location.getWeather().getMoodModifier() * 10);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Character that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Character{name='" + name + "', mood=" + moodLevel + "}";
    }
}