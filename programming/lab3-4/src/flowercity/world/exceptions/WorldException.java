package flowercity.world.exceptions;

/**
 * Базовое проверяемое исключение для ошибок мира Цветочного города.
 */
public class WorldException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public WorldException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message описание ошибки
     * @param cause   исходная причина
     */
    public WorldException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Возвращает сообщение об ошибке в удобочитаемом виде.
     */
    @Override
    public String getMessage() {
        return "Ошибка мира Цветочного города: " + super.getMessage();
    }
}