package flowercity.world.locations;

import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

/**
 * Река в Цветочном городе.
 */
public final class River extends FlowerCityLocation {
    private final double flowSpeed;

    /**
     * Создаёт реку.
     *
     * @param name       название
     * @param desc       описание
     * @param flowSpeed  скорость течения
     */
    public River(String name, String desc, double flowSpeed) {
        super(name, desc, Weather.getRandomWeather());
        this.flowSpeed = flowSpeed;
    }

    /**
     * @return скорость течения реки
     */
    public double getFlowSpeed() {
        return flowSpeed;
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }
}