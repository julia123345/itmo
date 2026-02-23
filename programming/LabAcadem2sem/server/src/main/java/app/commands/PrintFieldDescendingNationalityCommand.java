package app.commands;

import app.User;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Country;
import app.model.Person;
import app.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда print_field_descending_nationality :
 * выводит значения поля nationality всех элементов в порядке убывания.
 */
public class PrintFieldDescendingNationalityCommand extends ServerCollectionCommand {

    public PrintFieldDescendingNationalityCommand(Server server, CollectionManager collectionManager) {
        super(server,
                collectionManager,
                "print_field_descending_nationality",
                "Выводит значения поля nationality всех элементов в порядке убывания",
                CommandType.PRINT_FIELD_DESCENDING_NATIONALITY);
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
        String result = coll.stream()
                .map(Person::getNationality)
                .filter(n -> n != null)
                .sorted(Comparator.<Country>naturalOrder().reversed())
                .map(Enum::name)
                .collect(Collectors.joining("\n"));
        if (result.isEmpty()) {
            getServer().sendOK(key, "У элементов не задано поле nationality");
        } else {
            getServer().sendOK(key, result);
        }
        return true;
    }
}

