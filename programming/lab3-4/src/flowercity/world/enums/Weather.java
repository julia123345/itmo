package flowercity.world.enums;

import java.util.Random;

/**
 * Типы погоды, влияющие на настроение персонажей.
 */
public enum Weather {
    SUNNY("солнечная", 1),
    CLOUDY("пасмурная", 0),
    RAINY("дождливая", -1);

    private static final Random RND = new Random();
    private final String description;
    private final int moodModifier;

    Weather(String description, int moodModifier) {
        this.description = description;
        this.moodModifier = moodModifier;
    }

    /**
     * @return модификатор настроения для данной погоды
     */
    public int getMoodModifier() {
        return moodModifier;
    }

    /**
     * @return текстовое описание погоды
     */
    public String getDescription() {
        return description;
    }

    public static Weather getRandomWeather() {
        return values()[RND.nextInt(values().length)];
    }
}