package flowercity.world.locations;

import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

import java.util.Objects;

/**
 * Базовый тип локаций Цветочного города.
 */
public abstract sealed class FlowerCityLocation permits City, River, Bridge, Hill, Road {
    private final String name;
    private final String description;
    private final Weather weather;

    /**
     * Создаёт локацию.
     *
     * @param name        название локации
     * @param description краткое описание
     * @param weather     погода в локации
     */
    protected FlowerCityLocation(String name, String description, Weather weather) {
        this.name = name;
        this.description = description;
        this.weather = weather;
    }

    /**
     * Принимает посетителя мира.
     *
     * @param visitor посетитель
     */
    public abstract void accept(WorldVisitor visitor);

    /**
     * @return название локации
     */
    public String getName() {
        return name;
    }

    /**
     * @return описание локации
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return погода в локации
     */
    public Weather getWeather() {
        return weather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowerCityLocation that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "FlowerCityLocation{name='" + name + "', description='" + description + "', weather=" + weather + "}";
    }
}