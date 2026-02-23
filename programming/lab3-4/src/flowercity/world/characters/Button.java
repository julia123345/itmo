package flowercity.world.characters;

import flowercity.world.interfaces.WorldVisitor;
import flowercity.world.locations.*;
import flowercity.world.vehicles.Steamboat;

import java.util.Objects;

/**
 * Персонаж Кнопочка, умеющий наблюдать за объектами мира
 * и реагировать на локации и пароход.
 */
public final class Button extends Traveler implements WorldVisitor {
    private boolean hasSeenRealSteamboat;
    private final boolean knowsFromPictures = true;

    /**
     * Создаёт Кнопочку.
     *
     * @param name                 имя персонажа
     * @param hasSeenRealSteamboat видела ли Кнопочка настоящий пароход
     */
    public Button(String name, boolean hasSeenRealSteamboat) {
        super(name);
        this.hasSeenRealSteamboat = hasSeenRealSteamboat;
    }

    @Override
    protected String getArrivalVerb() {
        return "прибыла";
    }

    @Override
    public void visit(Road road) {
        logArrival(road);
        if (road.isWinding()) {
            log("Дорога была очень извилистой: она вилась " + road.getLandscape().getDescription() + ".");
        }
    }

    @Override
    public void visit(Hill hill) {
        logArrival(hill);
        log("Перед Кнопочкой с высоты " + hill.getElevation() + " метров мир казался игрушечным.");
    }

    @Override
    public void visit(River river) {
        logArrival(river);
        if (river.getFlowSpeed() > 2.0) {
            log("Перед ней река текла очень быстро");
        }
    }

    @Override
    public void visit(Steamboat sb) {
        log("Перед ней возник " + sb.getDescription());
        if (hasSeenRealSteamboat) {
            log("Она уже видела такое в большом городе, поэтому не сильно удивилась.");
        } else if (knowsFromPictures) {
            log(getName() + " никогда не видела настоящего парохода, но сразу узнала его по картинкам в книжках!");
        }
    }

    @Override
    public void visit(City city) {
        logArrival(city);
        log("После посещения города " + city.getName() + " Кнопочка стала опытнее.");
        this.hasSeenRealSteamboat = true;
    }

    public boolean hasSeenRealSteamboat() {
        return hasSeenRealSteamboat;
    }

    public boolean knowsFromPictures() {
        return knowsFromPictures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Button button)) return false;
        if (!super.equals(o)) return false;
        return hasSeenRealSteamboat == button.hasSeenRealSteamboat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hasSeenRealSteamboat);
    }

    @Override
    public String toString() {
        return "Button{name='" + getName() + "', hasSeenRealSteamboat=" + hasSeenRealSteamboat + ", mood=" + getMoodLevel() + "}";
    }
}