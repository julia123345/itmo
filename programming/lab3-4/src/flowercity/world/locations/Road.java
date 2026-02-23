package flowercity.world.locations;

import flowercity.world.enums.Landscape;
import flowercity.world.enums.Weather;
import flowercity.world.interfaces.WorldVisitor;

/**
 * Дорога, по которой может путешествовать Кнопочка.
 */
public final class Road extends FlowerCityLocation {
    private final boolean isWinding;
    private final Landscape landscape;

    /**
     * Создаёт дорогу.
     *
     * @param name       название
     * @param desc       описание
     * @param isWinding  является ли дорога извилистой
     * @param landscape  окружающий ландшафт
     */
    public Road(String name, String desc, boolean isWinding, Landscape landscape) {
        super(name, desc, Weather.getRandomWeather());
        this.isWinding = isWinding;
        this.landscape = landscape;
    }

    /**
     * @return является ли дорога извилистой
     */
    public boolean isWinding() {
        return isWinding;
    }

    /**
     * @return ландшафт вдоль дороги
     */
    public Landscape getLandscape() {
        return landscape;
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }
}