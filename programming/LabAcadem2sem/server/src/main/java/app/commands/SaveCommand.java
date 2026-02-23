package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;

import java.nio.channels.SelectionKey;

/**
 * Сохранение коллекции в файл. Доступна только на сервере (клиент эту команду отправить не может).
 */
public class SaveCommand extends ServerCollectionCommand {

    public SaveCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "save", "Сохраняет коллекцию в файл (только сервер)",
                CommandType.SAVE);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        if (collectionManager.saveToFile()) {
            getServer().sendOK(key, "Коллекция успешно сохранена");
        } else {
            getServer().sendError(key, "Ошибка сохранения");
        }
        return true;
    }
}