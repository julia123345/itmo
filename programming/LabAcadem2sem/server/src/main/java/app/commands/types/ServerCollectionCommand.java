package app.commands.types;

import app.commands.CommandType;
import app.server.Server;
import app.managers.CollectionManager;

/**
 * Класс для команд, требующих для выполнения коллекцию
 */
public abstract class ServerCollectionCommand extends ServerCommand {


    protected CollectionManager collectionManager;

    public ServerCollectionCommand(Server server,
                                   CollectionManager collectionManager,
                                   String commandName,
                                   String description,
                                   CommandType commandType) {
        super(server, commandName, description, commandType);
        this.collectionManager = collectionManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }


}
