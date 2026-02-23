package app;

/**
 * Запрос от конкретного пользователя.
 * Дополнительно к данным команды содержит учётную запись {@link User}.
 */
public class UserRequest extends Request {

    private User user;

    public UserRequest(User user, String commandLine, Object attachedObject) {
        super(commandLine, attachedObject);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
