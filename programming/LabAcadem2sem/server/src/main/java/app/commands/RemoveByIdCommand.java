package app.commands;

import app.User;
import app.model.Person;
import app.server.Server;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;

import java.nio.channels.SelectionKey;

public class RemoveByIdCommand extends ServerCollectionCommand {

    public RemoveByIdCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "remove_by_id {id}",
                "Удаляет элемент с заданным ID", CommandType.REMOVE_BY_ID);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 1) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(key, "ID должно быть целым числом");
            return false;
        }
        Person person = collectionManager.getById(id);
        if (person == null) {
            getServer().sendError(key, "Элемент с таким ID не найден");
            return false;
        }
        if (!person.getOwnerLogin().equals(user.getLogin())) {
            getServer().sendError(key, "Элемент c ID " + id + " принадлежит пользователю " + person.getOwnerLogin());
            return false;
        }
        collectionManager.deleteById(id);
        getServer().sendOK(key, "Элемент с ID " + id + " успешно удален");
        return true;
    }
}