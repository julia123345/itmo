package flowercity.world.exceptions;

/**
 * Проверяемое исключение, описывающее проблемы с перемещением персонажа.
 */
public class TravelException extends Exception {

    /**
     * Создаёт исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public TravelException(String message) {
        super(message);
    }

    /**
     * Возвращает сообщение об ошибке в удобочитаемом виде.
     */
    @Override
    public String getMessage() {
        return "Ошибка путешествия: " + super.getMessage();
    }
}