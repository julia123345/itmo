package app.commands;

import app.ResponseStatus;
import app.User;
import app.commands.types.ServerCommand;
import app.server.Server;
import java.nio.channels.SelectionKey;

public class AuthCommand extends ServerCommand {
    public AuthCommand(Server server) {
        super(server, "auth", "Авторизация", CommandType.AUTH);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length < 2) {
            getServer().sendError(key, "Использование: auth <login> <password>");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];

        if (getServer().getAuthManager().verify(login, password)) {
            // Важный момент: сохраняем пользователя в аттачмент ключа
            User authenticatedUser = new User(login, password);
            key.attach(authenticatedUser);
            getServer().sendAnother(key, ResponseStatus.AUTH_PASSED, "Авторизация успешна.");
        } else {
            getServer().sendAnother(key, ResponseStatus.AUTH_FAILED, "Ошибка: неверный логин или пароль.");
        }
        return true;
    }
}
