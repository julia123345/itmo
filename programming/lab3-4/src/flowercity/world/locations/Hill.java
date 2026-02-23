package flowercity.world.locations;

import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

/**
 * Холм, с которого открывается вид на Цветочный город.
 */
public final class Hill extends FlowerCityLocation {
    private final int elevation;

    /**
     * Создаёт холм.
     *
     * @param name       название
     * @param desc       описание
     * @param elevation  высота холма
     */
    public Hill(String name, String desc, int elevation) {
        super(name, desc, Weather.getRandomWeather());
        this.elevation = elevation;
    }

    /**
     * @return высота холма
     */
    public int getElevation() {
        return elevation;
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }
}