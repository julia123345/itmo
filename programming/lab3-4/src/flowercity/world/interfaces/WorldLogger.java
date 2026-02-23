package flowercity.world.interfaces;

/**
 * Функциональный интерфейс для логирования событий мира.
 */
@FunctionalInterface
public interface WorldLogger {

    /**
     * Логирует текст сообщения.
     *
     * @param message сообщение для вывода
     */
    void log(String message);
} 