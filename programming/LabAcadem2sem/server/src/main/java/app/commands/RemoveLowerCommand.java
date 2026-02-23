package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerObjectableCommand;
import app.managers.CollectionManager;
import app.model.Person;

import java.nio.channels.SelectionKey;

public class RemoveLowerCommand extends ServerObjectableCommand {

    public RemoveLowerCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "remove_lower",
                "Удаляет элементы коллекции, меньшие заданного (только ваши)", CommandType.REMOVE_LOWER);
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
        collectionManager.removeLower(user, person);
        getServer().sendOK(key, "Элементы, меньшие заданного, удалены");
        return true;
    }
}