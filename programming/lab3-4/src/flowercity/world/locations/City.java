package flowercity.world.locations;

import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

/**
 * Город, в котором живут жители Цветочного города.
 */
public final class City extends FlowerCityLocation {
    private final int population;

    /**
     * Создаёт город.
     *
     * @param name        название
     * @param description описание
     * @param population  численность населения
     */
    public City(String name, String description, int population) {
        super(name, description, Weather.getRandomWeather());
        this.population = population;
    }

    /**
     * @return численность населения города
     */
    public int getPopulation() {
        return population;
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }
}