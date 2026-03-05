package app.commands;

import app.Client;
import app.User;
import app.UserRequest;
import app.commands.types.ClientCommand;

import java.io.IOException;

public class RegisterCommand extends ClientCommand {

    public RegisterCommand(Client client) {
        super(client, "register", "Регистрация", CommandType.REGISTER);
    }

    @Override
    public boolean execute(String line, String[] arguments) {
        try {
            getConsole().println("Создание нового пользователя");
            getConsole().println("Введите логин:");
            String login = getConsole().readLine();
            if (login == null || login.trim().isEmpty()) {
                getConsole().printErr("Логин не может быть пустым");
                return false;
            }

            getConsole().println("Введите пароль:");
            String password = getConsole().readLine();
            if (password == null || password.trim().isEmpty()) {
                getConsole().printErr("Пароль не может быть пустым");
                return false;
            }

            getClient().requestToServer(
                    new UserRequest(null, "register " + login.trim() + " " + password.trim(), null)
            );
            return true;

        } catch (IOException e) {
            getConsole().printErr("Ошибка при регистрации: " + e.getMessage());
            return false;
        } catch (Exception e) {
            getConsole().printErr("Неожиданная ошибка: " + e.getMessage());
            return false;
        }
    }
}