package app.commands;

import app.User;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Person;
import app.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Collection;
import java.util.Comparator;

/**
 * Команда min_by_name : выводит любой объект из коллекции,
 * значение поля name которого является минимальным.
 */
public class MinByNameCommand extends ServerCollectionCommand {

    public MinByNameCommand(Server server, CollectionManager collectionManager) {
        super(server,
                collectionManager,
                "min_by_name",
                "Выводит объект, значение поля name которого минимально",
                CommandType.MIN_BY_NAME);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        Collection<Person> coll = collectionManager.getUnmodifiableCollection();
        if (coll.isEmpty()) {
            getServer().sendOK(key, "Коллекция пуста");
            return true;
        }
        Person min = coll.stream()
                .min(Comparator.comparing(Person::getName))
                .orElse(null);
        if (min == null) {
            getServer().sendOK(key, "Коллекция пуста");
        } else {
            getServer().sendOK(key, min.toString());
        }
        return true;
    }
}

