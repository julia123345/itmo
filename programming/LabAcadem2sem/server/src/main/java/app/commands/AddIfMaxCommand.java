package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerObjectableCommand;
import app.managers.CollectionManager;
import app.model.Person;

import java.nio.channels.SelectionKey;

public class AddIfMaxCommand extends ServerObjectableCommand {

    public AddIfMaxCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "add_if_max", "Добавляет элемент, если он больше максимального в коллекции",
                CommandType.ADD_IF_MAX);
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
            person.setId(generateId());
            person.setOwnerLogin(user.getLogin());
            person.setCreationDate(java.time.ZonedDateTime.now());
        } catch (Exception e) {
            getServer().sendError(key, "Некорректный объект Person");
            return false;
        }
        if (person == null) {
            getServer().sendError(key, "Неправильный объект Person");
            return false;
        }
        Person maxEl = collectionManager.getMaxElement();
        if (maxEl == null || person.compareTo(maxEl) > 0) {
            collectionManager.add(person);
            getServer().sendOK(key, "Элемент успешно добавлен в коллекцию");
        } else {
            getServer().sendOK(key, "Элемент не добавлен (меньше максимального)");
        }
        return true;
    }

    private long generateId() {
        CollectionManager manager = getServer().getCollectionManager();
        for (long i = 1; i <= 100000000; i++) {
            if (manager.getById(i) == null) return i;
        }
        return 0;
    }
}