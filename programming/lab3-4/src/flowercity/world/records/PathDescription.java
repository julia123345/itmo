package flowercity.world.records;

/**
 * Описание пути между двумя точками.
 *
 * @param startPoint начальная точка
 * @param endPoint   конечная точка
 * @param distance   длина пути в километрах
 */
public record PathDescription(String startPoint,
                              String endPoint,
                              double distance) {

    /**
     * @return человекочитаемое описание пути
     */
    public String getInfo() {
        return "Путь из " + startPoint +
                " в " + endPoint +
                " длиной " + distance + " км";
    }
}