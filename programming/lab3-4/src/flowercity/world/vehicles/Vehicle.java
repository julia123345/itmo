package flowercity.world.vehicles;

import flowercity.world.interfaces.WorldVisitor;

/**
 * Абстрактное транспортное средство в мире Цветочного города.
 */
public abstract sealed class Vehicle permits Steamboat {
    private final String name;
    private int currentSpeed = 0;

    /**
     * Создаёт транспортное средство с указанным именем.
     *
     * @param name название транспорта
     */
    protected Vehicle(String name) {
        this.name = name;
    }

    /**
     * @return название транспортного средства
     */
    public String getName() {
        return name;
    }

    /**
     * Описание транспорта для вывода в сценарии.
     *
     * @return человекочитаемое описание
     */
    public abstract String getDescription();

    /**
     * Принимает посетителя мира для паттерна Visitor.
     *
     * @param visitor посетитель
     */
    public abstract void accept(WorldVisitor visitor);

    /**
     * @return текущая скорость
     */
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * Устанавливает текущую скорость.
     *
     * @param speed новая скорость
     */
    public void setCurrentSpeed(int speed) {
        this.currentSpeed = speed;
    }
}