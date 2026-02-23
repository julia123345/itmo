package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;

import java.nio.channels.SelectionKey;

public class ClearCommand extends ServerCollectionCommand {

    public ClearCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "clear", "Очищает ваши элементы коллекции",
                CommandType.CLEAR);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        collectionManager.clear(user);
        getServer().sendOK(key, "Ваши элементы коллекции удалены");
        return true;
    }
}