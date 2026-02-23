package app.commands;

import app.User;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Person;
import app.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Collection;

/**
 * Команда average_of_height : выводит среднее значение поля height для всех элементов коллекции.
 */
public class AverageOfHeightCommand extends ServerCollectionCommand {

    public AverageOfHeightCommand(Server server, CollectionManager collectionManager) {
        super(server,
                collectionManager,
                "average_of_height",
                "Выводит среднее значение поля height для всех элементов коллекции",
                CommandType.AVERAGE_OF_HEIGHT);
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
        double avg = coll.stream()
                .mapToLong(p -> p.getHeight() == null ? 0L : p.getHeight())
                .average()
                .orElse(0.0);
        getServer().sendOK(key, "Среднее значение height: " + avg);
        return true;
    }
}

