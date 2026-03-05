package app.commands.types;

import app.User;
import app.commands.CommandType;
import app.server.Server;
import app.managers.CollectionManager;

import java.nio.channels.SelectionKey;

public abstract class ServerObjectableCommand extends ServerCollectionCommand {

    public ServerObjectableCommand(Server server,
                                   CollectionManager collectionManager,
                                   String commandName,
                                   String description,
                                   CommandType commandType) {
        super(server, collectionManager, commandName, description, commandType);
    }

    public abstract boolean execute(User user, SelectionKey key, String[] args, Object object);


}
