package flowercity.world.vehicles;

import flowercity.world.interfaces.WorldVisitor;

import java.util.Objects;

/**
 * Пароход, на котором Кнопочка может путешествовать по реке.
 */
public final class Steamboat extends Vehicle {
    private final boolean hasBigPipe;
    private boolean isProducingSmoke;
    private int fuelLevel;
    private double pollutionLevel;

    /**
     * Создаёт пароход.
     *
     * @param name        название парохода
     * @param hasBigPipe  есть ли большая труба
     * @param initialFuel начальный запас топлива
     */
    public Steamboat(String name, boolean hasBigPipe, int initialFuel) {
        super(name);
        this.hasBigPipe = hasBigPipe;
        this.fuelLevel = initialFuel;
    }

    public void setProducingSmoke(boolean smoke) {
        this.isProducingSmoke = smoke;
        if (smoke) this.pollutionLevel += 0.5;
    }

    public void consumeFuel() {
        this.fuelLevel -= 1;
    }

    @Override
    public String getDescription() {
        String pipeDesc = hasBigPipe ? " с большой трубой" : " с маленькой трубой";
        String smokeDesc = isProducingSmoke ? ", из которой валят клубы дыма" : "";
        return "пароход " + getName() + pipeDesc + smokeDesc + " (Запас топлива: " + fuelLevel + ")";
    }

    @Override
    public void accept(WorldVisitor visitor) {
        visitor.visit(this);
    }

    public boolean hasBigPipe() {
        return hasBigPipe;
    }

    public boolean isProducingSmoke() {
        return isProducingSmoke;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public double getPollutionLevel() {
        return pollutionLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Steamboat that)) return false;
        return hasBigPipe == that.hasBigPipe
                && isProducingSmoke == that.isProducingSmoke
                && fuelLevel == that.fuelLevel
                && Double.compare(that.pollutionLevel, pollutionLevel) == 0
                && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), hasBigPipe, isProducingSmoke, fuelLevel, pollutionLevel);
    }

    @Override
    public String toString() {
        return "Steamboat{name='" + getName() + "', hasBigPipe=" + hasBigPipe
                + ", isProducingSmoke=" + isProducingSmoke
                + ", fuelLevel=" + fuelLevel
                + ", pollutionLevel=" + pollutionLevel + "}";
    }
}