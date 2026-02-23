package app.commands;

import app.User;
import app.server.Server;
import app.commands.types.ServerCommand;
import app.managers.ServerCommandManager;

import java.nio.channels.SelectionKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExecuteScriptCommand extends ServerCommand {

    private final List<String> activeScripts = new ArrayList<>();
    private final ServerCommandManager commandManager;

    public ExecuteScriptCommand(Server server, ServerCommandManager commandManager) {
        super(server, "execute_script", "Выполняет скрипт из файла", CommandType.EXECUTE_SCRIPT);
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(User user, SelectionKey key, String[] arguments) {
        if (arguments.length != 1) {
            getServer().sendError(key, "Неверное использование команды.");
            return false;
        }
        String file = arguments[0];
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(file));
        } catch (Exception e) {
            getServer().sendError(key, "Ошибка чтения файла: " + e.getMessage());
            return false;
        }
        if (lines == null || lines.isEmpty()) {
            getServer().sendOK(key, "Файл пуст");
            return false;
        }
        if (activeScripts.contains(file)) {
            getServer().sendOK(key, "Попытка создать бесконечную рекурсию");
            return false;
        }
        activeScripts.add(file);
        lines.forEach(line -> commandManager.executeCommand(user, key, line.trim()));
        activeScripts.remove(file);
        return true;
    }
}