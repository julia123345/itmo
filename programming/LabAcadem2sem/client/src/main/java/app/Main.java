package app;

import app.commands.*;
import ru.bright.commands.*;
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
            try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }

        Client client = new Client(console, host, port);
        ClientCommandManager commandManager = new ClientCommandManager(client);
        pullCommands(commandManager, client);
        System.out.println("Клиент запущен. Для входа введите: auth  или  register");
        client.setCommandManager(commandManager);
        try {
            client.openConnection();
            client.startListening();
        } catch (IOException e) {
            console.printErr("Error while opening connection: " + e.getMessage());
        }
    }

    private static void pullCommands(CommandManager commandManager, Client client) {
        commandManager.registerCommand(new AddCommand(client));
        commandManager.registerCommand(new AddIfMaxCommand(client));
        commandManager.registerCommand(CommandType.CLEAR);
        commandManager.registerCommand(CommandType.EXECUTE_SCRIPT);
        commandManager.registerCommand(new ExitCommand(client));
        // по ТЗ: filter_start* необязательна, но можно оставить как доп.
        commandManager.registerCommand(CommandType.FILTER_STARTS_WITH_NAME);
        commandManager.registerCommand(CommandType.HISTORY);
        commandManager.registerCommand(CommandType.INFO);
        commandManager.registerCommand(CommandType.PRINT_ASCENDING);
        commandManager.registerCommand(CommandType.PRINT_DESCENDING);
        // команды авторизации
        commandManager.registerCommand(new RegisterCommand(client));
        commandManager.registerCommand(new LoginCommand(client));
        commandManager.registerCommand(CommandType.REMOVE_BY_ID);
        commandManager.registerCommand(new RemoveLowerCommand(client));
        commandManager.registerCommand(new RemoveGreaterCommand(client));
        // клиентская команда SAVE по ТЗ должна быть убрана — оставляем закомментированной
        // commandManager.registerCommand(CommandType.SAVE);
        commandManager.registerCommand(CommandType.SHOW);
        commandManager.registerCommand(new UpdateCommand(client));
        // дополнительные команды из ТЗ, не требующие составных объектов
        commandManager.registerCommand(CommandType.REMOVE_HEAD);
        commandManager.registerCommand(CommandType.AVERAGE_OF_HEIGHT);
        commandManager.registerCommand(CommandType.MIN_BY_NAME);
        commandManager.registerCommand(CommandType.PRINT_FIELD_DESCENDING_NATIONALITY);
        commandManager.registerCommand(CommandType.HELP);
    }
}