package app.commands.types;

import app.User;
import app.commands.Command;
import app.commands.CommandType;
import app.server.Server;

import java.nio.channels.SelectionKey;

public abstract class ServerCommand extends Command {

    private Server server;

    /**
     * Базовый конструктор серверной команды.
     *
     * @param server      сервер, на котором выполняется команда
     * @param commandName строковое имя команды (как вводится пользователем)
     * @param description описание команды для справки
     * @param commandType тип команды
     */
    public ServerCommand(Server server, String commandName, String description, CommandType commandType) {
        super(commandName, description, commandType);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public abstract boolean execute(User user, SelectionKey key, String[] args);
}
