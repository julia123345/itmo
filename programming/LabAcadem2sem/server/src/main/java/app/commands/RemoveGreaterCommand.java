package app.commands;

import app.User;
import app.commands.types.ServerObjectableCommand;
import app.managers.CollectionManager;
import app.model.Person;
import app.server.Server;

import java.nio.channels.SelectionKey;

/**
 * Команда remove_greater {element}: удаляет из коллекции все элементы,
 * строго превышающие заданный элемент и принадлежащие текущему пользователю.
 */
public class RemoveGreaterCommand extends ServerObjectableCommand {

    public RemoveGreaterCommand(Server server, CollectionManager collectionManager) {
        super(server,
                collectionManager,
                "remove_greater",
                "Удаляет элементы коллекции, превышающие заданный (только ваши)",
                CommandType.REMOVE_GREATER);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        getServer().sendError(key, "Неверное использование команды.");
        return false;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] args, Object object) {
        if (args.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        Person person;
        try {
            person = (Person) object;
        } catch (Exception e) {
            getServer().sendError(key, "Неверный объект");
            return false;
        }
        if (person == null) {
            getServer().sendError(key, "Неверный объект");
            return false;
        }
        collectionManager.removeGreater(user, person);
        getServer().sendOK(key, "Элементы, превышающие заданный, удалены");
        return true;
    }
}

