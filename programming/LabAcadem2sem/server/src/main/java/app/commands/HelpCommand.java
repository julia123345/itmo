package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;

/**
 * Команда для вывода справки
 */
public class HelpCommand extends ServerCommand {

    private CommandManager commandManager;
    public HelpCommand(Server server, CommandManager commandManager) {
        super(server, "help", "Выводит справку по командам", CommandType.HELP);
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendError(key,"Неверное использование команды.");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Справка:\n");
        commandManager.getCommands().forEach(command -> {
            sb.append(command.getCommandName()).append(" - ").append(command.getDescription()).append("\n");
        });
        getServer().sendOK(key,sb.toString());
        return true;
    }
}