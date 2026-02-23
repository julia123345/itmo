package flowercity.world.exceptions;

/**
 * Непроверяемое исключение, описывающее некорректное состояние мира.
 */
public class IllegalStateWorldException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public IllegalStateWorldException(String message) {
        super(message);
    }

    /**
     * Возвращает сообщение об ошибке в удобочитаемом виде.
     */
    @Override
    public String getMessage() {
        return "Неверное состояние мира: " + super.getMessage();
    }
}