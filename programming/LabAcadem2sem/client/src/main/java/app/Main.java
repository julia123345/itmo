package app;

import app.commands.*;
import app.util.BasicConsole;
import app.util.ClientCommandManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        BasicConsole console = new BasicConsole();
        String host = "127.0.0.1";
        int port = 8080;

        if (args != null && args.length >= 1) {
            host = args[0];
        }
        if (args != null && args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        Client client = new Client(console, host, port);
        ClientCommandManager commandManager = new ClientCommandManager(client);

        registerCommands(commandManager, client);

        console.println("Клиент запущен. Для входа введите: auth или register");
        console.println("Для выхода введите: exit");

        client.setCommandManager(commandManager);

        try {
            if (client.openConnection()) {
                client.startListening();
            } else {
                console.printErr("Не удалось подключиться к серверу. Проверьте доступность сервера.");
            }
        } catch (IOException e) {
            console.printErr("Ошибка при работе с сервером: " + e.getMessage());
        }
    }

    private static void registerCommands(CommandManager commandManager, Client client) {
        // Команды, требующие ввода данных
        commandManager.registerCommand(new AddCommand(client));
        commandManager.registerCommand(new AddIfMaxCommand(client));
        commandManager.registerCommand(new UpdateCommand(client));
        commandManager.registerCommand(new RemoveLowerCommand(client));
        commandManager.registerCommand(new RemoveGreaterCommand(client));

        // Команды авторизации
        commandManager.registerCommand(new RegisterCommand(client));
        commandManager.registerCommand(new LoginCommand(client));
        commandManager.registerCommand(new ExitCommand(client));

        // Серверные команды (без клиентской обработки)
        commandManager.registerCommand(CommandType.CLEAR);
        commandManager.registerCommand(CommandType.EXECUTE_SCRIPT);
        commandManager.registerCommand(CommandType.FILTER_STARTS_WITH_NAME);
        commandManager.registerCommand(CommandType.HISTORY);
        commandManager.registerCommand(CommandType.INFO);
        commandManager.registerCommand(CommandType.PRINT_ASCENDING);
        commandManager.registerCommand(CommandType.PRINT_DESCENDING);
        commandManager.registerCommand(CommandType.REMOVE_BY_ID);
        commandManager.registerCommand(CommandType.SHOW);
        commandManager.registerCommand(CommandType.REMOVE_HEAD);
        commandManager.registerCommand(CommandType.AVERAGE_OF_HEIGHT);
        commandManager.registerCommand(CommandType.MIN_BY_NAME);
        commandManager.registerCommand(CommandType.PRINT_FIELD_DESCENDING_NATIONALITY);
        commandManager.registerCommand(CommandType.HELP);
        commandManager.registerCommand(CommandType.GENERATE_RANDOM); // новый
    }
}