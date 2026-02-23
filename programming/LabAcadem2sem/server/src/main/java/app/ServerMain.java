package app;

import app.commands.*;
import app.managers.AuthManager;
import app.managers.CollectionFileManager;
import app.managers.CollectionManager;
import app.managers.ServerCommandManager;
import app.commands.*;
import app.managers.*;
import app.server.Server;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {

        if (args == null || args.length < 1) {
            LOGGER.severe("Необходимо передать путь к XML-файлу коллекции в аргументе командной строки!");
            System.exit(1);
        }

        String collectionPath = args[0];
        String usersPath = (args.length >= 2)
                ? args[1]
                : System.getProperty("user.dir") + File.separator + "users.txt";

        int portNumber = 8080;

        Server server = new Server(portNumber);
        server.setLogger(LOGGER);

        ServerCommandManager commandManager = new ServerCommandManager(server);
        server.setServerCommandManager(commandManager);

        CollectionFileManager fileManager = new CollectionFileManager(server, collectionPath);
        CollectionManager collectionManager = new CollectionManager(server, fileManager);
        server.setCollectionManager(collectionManager);

        AuthManager authManager = new AuthManager(server, usersPath);
        server.setAuthManager(authManager);
        authManager.init();

        registerCommands(server, commandManager, collectionManager);

        if (collectionManager.loadFromFile()) {
            LOGGER.log(Level.INFO, "Collection loaded from XML file");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.startServer();
    }

    private static void registerCommands(Server server,
                                         ServerCommandManager commandManager,
                                         CollectionManager collectionManager) {

        commandManager.registerCommand(new AddCommand(server, collectionManager));
        commandManager.registerCommand(new AddIfMaxCommand(server, collectionManager));
        commandManager.registerCommand(new ClearCommand(server, collectionManager));
        commandManager.registerCommand(new ExecuteScriptCommand(server, commandManager));
        commandManager.registerCommand(new HistoryCommand(server, commandManager));
        commandManager.registerCommand(new InfoCommand(server, collectionManager));
        commandManager.registerCommand(new PrintAscendingCommand(server, collectionManager));
        commandManager.registerCommand(new PrintDescendingCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveByIdCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveLowerCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveGreaterCommand(server, collectionManager));
        commandManager.registerCommand(new RemoveHeadCommand(server, collectionManager));
        commandManager.registerCommand(new AverageOfHeightCommand(server, collectionManager));
        commandManager.registerCommand(new MinByNameCommand(server, collectionManager));
        commandManager.registerCommand(new PrintFieldDescendingNationalityCommand(server, collectionManager));

        // save остаётся ТОЛЬКО на сервере
        commandManager.registerCommand(new SaveCommand(server, collectionManager));

        commandManager.registerCommand(new ShowCommand(server, collectionManager));
        commandManager.registerCommand(new UpdateCommand(server, collectionManager));
        commandManager.registerCommand(new HelpCommand(server, commandManager));
        commandManager.registerCommand(new RegisterCommand(server));
        commandManager.registerCommand(new AuthCommand(server));
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}