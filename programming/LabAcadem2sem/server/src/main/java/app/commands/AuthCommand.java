package app.commands;

import app.ResponseStatus;
import app.User;
import app.commands.types.ServerCommand;
import app.server.Server;

import java.nio.channels.SelectionKey;

public class AuthCommand extends ServerCommand {

    public AuthCommand(Server server) {
        super(server,
                "auth", "Авторизация",
                CommandType.AUTH);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length < 2) {
            getServer().sendError(key,"Неверное использование команды.");
            return true;
        }
        String login = arguments[0];
        String password = arguments[1];
        if(getServer().getAuthManager().verify(login,password)) {
            getServer().sendAnother(key, ResponseStatus.AUTH_PASSED, "Login successful.");
        } else {
            getServer().sendAnother(key,ResponseStatus.AUTH_FAILED, "Auth failed.");
        }
        return true;
    }
}