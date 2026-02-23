package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCommand;

import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Команда для вывода истории команд
 */
public class HistoryCommand extends ServerCommand {

    private CommandManager commandManager;

    public HistoryCommand(Server server, CommandManager commandManager) {
        super(server,
                "history", "Выводит последние 13 команд",
                CommandType.HISTORY);
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if(arguments.length != 0) {
            getServer().sendError(key,"Неверное использование команды.");
            return false;
        }
        Queue<String> cmds = commandManager.getCommandHistory();
        if(cmds.isEmpty()) {
            getServer().sendOK(key,"История команд пуста");
            return true;
        }
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        sb.append("История команд:\n");
        cmds.forEach(cmd -> {
            sb.append(i.get()).append(". ").append(cmd).append("\n");
            i.incrementAndGet();
        });
        getServer().sendOK(key,sb.toString());
        return true;
    }
}