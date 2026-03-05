package app.commands;

import app.Client;
import app.User;
import app.UserRequest;
import app.commands.types.ClientCommand;
import java.io.IOException;

public class LoginCommand extends ClientCommand {
    public LoginCommand(Client client) {
        super(client, "auth", "Авторизация", CommandType.AUTH);
    }

    @Override
    public boolean execute(String line, String[] arguments) {
        try {
            getConsole().println("Введите логин:");
            String login = getConsole().readLine();
            getConsole().println("Введите пароль:");
            String password = getConsole().readLine();

            if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                getConsole().printErr("Логин и пароль не могут быть пустыми");
                return false;
            }

            User user = new User(login.trim(), password.trim());
            // Передаем объект user ПЕРВЫМ аргументом в UserRequest
            UserRequest req = new UserRequest(user, "auth " + login.trim() + " " + password.trim(), null);
            getClient().requestToServer(req);

            return true;
        } catch (IOException e) {
            getConsole().printErr("Ошибка ввода-вывода: " + e.getMessage());
            return false;
        }
    }
}
