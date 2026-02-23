package app.commands;

import app.User;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Person;
import app.server.Server;

import java.nio.channels.SelectionKey;
import java.util.Comparator;
import java.util.List;

/**
 * Команда remove_head : выводит первый элемент коллекции и удаляет его.
 * Модифицировать коллекцию может только владелец элемента.
 */
public class RemoveHeadCommand extends ServerCollectionCommand {

    public RemoveHeadCommand(Server server, CollectionManager collectionManager) {
        super(server,
                collectionManager,
                "remove_head",
                "Выводит первый элемент коллекции и удаляет его (если он ваш)",
                CommandType.REMOVE_HEAD);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }

        List<Person> list = collectionManager.getCollectionSortedByName();
        if (list == null || list.isEmpty()) {
            getServer().sendOK(key, "Коллекция пуста");
            return true;
        }

        // Первый элемент коллекции (минимальный в естественном порядке)
        Person head = list.stream()
                .min(Comparator.naturalOrder())
                .orElse(null);

        if (head == null) {
            getServer().sendOK(key, "Коллекция пуста");
            return true;
        }

        if (!user.getLogin().equals(head.getOwnerLogin())) {
            getServer().sendError(key,
                    "Первый элемент принадлежит пользователю " + head.getOwnerLogin());
            return false;
        }

        collectionManager.deleteById(head.getId());
        getServer().sendOK(key, "Удалён элемент: " + head);
        return true;
    }
}

