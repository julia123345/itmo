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
        if(arguments.length < 2) {
            getServer().sendError(key,"Неверное использование команды.");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];
        if (getServer().getAuthManager().hasLogin(login)) {
            getServer().sendError(key, "Login is already busy");
            return true;
        }
        if (getServer().getAuthManager().register(login, password)) {
            getServer().sendOK(key, "Register successful.");
        } else {
            getServer().sendError(key, "Registration failed.");
        }
        return true;
    }
}