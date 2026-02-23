package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerObjectableCommand;
import app.managers.CollectionManager;
import app.model.Person;

import java.nio.channels.SelectionKey;

public class UpdateCommand extends ServerObjectableCommand {

    public UpdateCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "update {id}", "Обновляет элемент в коллекции",
                CommandType.UPDATE);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        getServer().sendError(key, "Неверное использование команды.");
        return false;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] args, Object object) {
        if (args.length != 1) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            getServer().sendError(key, "ID должно быть целым числом");
            return false;
        }
        Person toUpdate = collectionManager.getById(id);
        if (toUpdate == null) {
            getServer().sendError(key, "Элемент с таким ID не найден");
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
        if (!toUpdate.getOwnerLogin().equals(user.getLogin())) {
            getServer().sendError(key, "Элемент c ID " + id + " принадлежит пользователю " + toUpdate.getOwnerLogin());
            return false;
        }
        person.setId(id);
        person.setOwnerLogin(user.getLogin());
        person.setCreationDate(toUpdate.getCreationDate());
        collectionManager.update(person);
        getServer().sendOK(key, "Элемент c ID " + id + " обновлен");
        return true;
    }
}