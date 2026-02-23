package flowercity.world.locations;

import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

/**
 * Мост, соединяющий берега реки.
 */
public final class Bridge extends FlowerCityLocation {
    private final double length;

    /**
     * Создаёт мост.
     *
     * @param name        название
     * @param description описание
     * @param length      длина моста
     */
    public Bridge(String name, String description, double length) {
        super(name, description, Weather.getRandomWeather());
        this.length = length;
    }

    /**
     * @return длина моста
     */
    public double getLength() {
        return length;
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }
}