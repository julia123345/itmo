package flowercity.world.exceptions;

/**
 * Исключение, сигнализирующее о недоступности локации.
 */
public class LocationInaccessibleException extends TravelException {

    /**
     * Создаёт исключение о недоступной локации.
     *
     * @param message описание причины недоступности
     */
    public LocationInaccessibleException(String message) {
        super(message);
    }

    /**
     * Возвращает специализированное сообщение об ошибке.
     */
    @Override
    public String getMessage() {
        // Убираем дублирование "Ошибка путешествия" (оно придет из super.getMessage())
        // и добавляем специфику
        return "Доступ заблокирован! " + super.getMessage();
    }
}
