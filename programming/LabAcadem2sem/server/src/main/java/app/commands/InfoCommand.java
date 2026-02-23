package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCollectionCommand;
import app.managers.CollectionManager;
import app.model.Person;

import java.nio.channels.SelectionKey;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class InfoCommand extends ServerCollectionCommand {

    public InfoCommand(Server server, CollectionManager collectionManager) {
        super(server, collectionManager,
                "info", "Выводит информацию о коллекции",
                CommandType.INFO);
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 0) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        Collection<Person> coll = collectionManager.getUnmodifiableCollection();
        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: ").append(coll.getClass().getSimpleName()).append("\n");
        sb.append("Количество элементов: ").append(coll.size()).append("\n");
        sb.append("Дата создания: ").append(CollectionManager.getCreationDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        getServer().sendOK(key, sb.toString());
        return true;
    }
}