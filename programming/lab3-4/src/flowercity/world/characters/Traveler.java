package flowercity.world.characters;

import flowercity.world.exceptions.TravelException;
import flowercity.world.interfaces.WorldVisitor;
import flowercity.world.locations.FlowerCityLocation;

import java.util.Random;

/**
 * Путешественник, который может перемещаться между локациями.
 */
public abstract class Traveler extends Character {
    private FlowerCityLocation currentLocation;

    /**
     * Создаёт путешественника с указанным именем.
     *
     * @param name имя персонажа
     */
    protected Traveler(String name) {
        super(name);
    }

    /**
     * Перемещает путешественника в указанную локацию.
     * Может выбросить {@link TravelException}, если переход невозможен.
     *
     * @param target целевая локация
     * @throws TravelException ошибка перемещения
     */
    public void moveTo(FlowerCityLocation target) throws TravelException {
        if (target == null) throw new TravelException("Некуда идти!");
        if (target.equals(currentLocation)) {
            throw new TravelException(getName() + " уже находится в " + target.getName());
        }
        if (new Random().nextDouble() < 0.1) {
            throw new TravelException(getName() + " столкнулся со случайным препятствием по пути!");
        }
        this.currentLocation = target;
        if (this instanceof WorldVisitor visitor) {
            target.accept(visitor);
        } else {
            logArrival(target);
        }
    }

    /**
     * @return текущая локация путешественника
     */
    public FlowerCityLocation getCurrentLocation() {
        return currentLocation;
    }
}