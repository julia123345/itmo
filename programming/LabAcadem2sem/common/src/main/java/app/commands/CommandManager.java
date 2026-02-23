package app.commands;

import java.util.*;

/**
 * Класс для управления командами
 * Хранит команды и выполняет их
 */
public abstract class CommandManager {

    /**
     * Словарь доступных команд
     */
    private Map<CommandType, Command> commands;

    /**
     * История выполненных команд
     */
    private Queue<String> commandHistory;

    public CommandManager() {
        this.commands = new HashMap<>();
        this.commandHistory = new ArrayDeque<>();
    }

    /**
     * Возвращает коллекцию доступных команд
     * @return доступные команды
     */
    public Collection<Command> getCommands() {
        return commands.values();
    }

    /**
     * Регистрирует команду для дальнейшей работы с ней
     * @param command команда
     */
    public void registerCommand(Command command) {
        commands.put(command.getCommandType(), command);
    }

    public void registerCommand(CommandType type) {
        commands.put(type, null);
    }

    /**
     * Возвращает историю команд
     * @return история команд
     */
    public Queue<String> getCommandHistory() {
        return commandHistory;
    }

    public Map<CommandType, Command> getCommandMap() {
        return commands;
    }
}