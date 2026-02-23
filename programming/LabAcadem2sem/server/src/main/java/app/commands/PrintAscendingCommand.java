package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Person;

import java.nio.channels.SelectionKey;
import java.util.List;

public class PrintAscendingCommand extends ServerCollectionCommand {

    public PrintAscendingCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "print_ascending", "Выводит коллекцию в порядке возрастания",
                CommandType.PRINT_ASCENDING);
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
        StringBuilder sb = new StringBuilder();
        list.forEach(p -> sb.append(p.toString()).append("\n"));
        getServer().sendOK(key, sb.toString());
        return true;
    }
}