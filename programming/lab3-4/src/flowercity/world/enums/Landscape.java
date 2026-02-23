package flowercity.world.enums;

/**
 * Типы ландшафта вдоль дороги.
 */
public enum Landscape {
    HILLS("холмами"), FOREST("лесом"), MIXED("то холмами, то лесом");
    private final String description;

    Landscape(String description) {
        this.description = description;
    }

    /**
     * @return текстовое описание ландшафта
     */
    public String getDescription() {
        return description;
    }
}