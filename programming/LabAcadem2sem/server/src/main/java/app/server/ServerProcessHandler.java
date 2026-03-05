package app.server;

import app.Response;
import app.ResponseStatus;
import app.User;
import app.UserRequest;
import java.nio.channels.SelectionKey;

public class ServerProcessHandler {
    private Server server;

    public ServerProcessHandler(Server server) {
        this.server = server;
    }

    protected void handleRequest(SelectionKey key, UserRequest request) {
        String commandLine = request.getCommandLine().trim();
        String commandName = commandLine.split("\\s+")[0].toLowerCase();

        // 1. Проверка: является ли команда регистрацией или логином
        boolean isAuthCommand = commandName.equals("auth") || commandName.equals("register");

        // 2. Достаем пользователя либо из запроса, либо из аттачмента ключа (сессии)
        User user = request.getUser();
        if (user == null && key.attachment() instanceof User) {
            user = (User) key.attachment();
        }

        if (!isAuthCommand) {
            // Для всех остальных команд проверяем валидность пользователя
            if (user == null || !server.getAuthManager().verify(user.getLogin(), user.getPassword())) {
                server.responseToClient(key, new Response(ResponseStatus.AUTH_FAILED, "Требуется авторизация или неверные данные."));
                return;
            }
        }

        // 3. Выполнение команды (логика остается прежней)
        if (request.getAttachedObject() == null) {
            server.getServerCommandManager().executeCommand(user, key, commandLine);
        } else {
            server.getServerCommandManager().executeObjectableCommand(user, key, commandLine, request.getAttachedObject());
        }
    }
}
