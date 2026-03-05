package app.commands;

import app.User;
import app.commands.types.ServerCommand;
import app.server.Server;

import java.nio.channels.SelectionKey;

public class RegisterCommand extends ServerCommand {

    public RegisterCommand(Server server) {
        super(server,
                "register", "Регистрация",
                CommandType.REGISTER);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        // Проверяем, что передано хотя бы 2 аргумента (логин и пароль)
        if(arguments.length < 2) {
            getServer().sendError(key, "Usage: register <login> <password>");
            return true;
        }

        String login = arguments[0];
        String password = arguments[1];

        if (getServer().getAuthManager().hasLogin(login)) {
            getServer().sendError(key, "Login '" + login + "' is already taken.");
            return true;
        }

        if (getServer().getAuthManager().register(login, password)) {
            getServer().sendOK(key, "Registration successful! Now you can 'auth'.");
        } else {
            getServer().sendError(key, "Internal server error during registration.");
        }
        return true;
    }

}