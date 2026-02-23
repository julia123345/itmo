package app.commands.types;

import app.Client;
import app.commands.Command;
import app.commands.CommandType;
import app.util.Console;

public abstract class ClientCommand extends Command {

    private Client client;

    public ClientCommand(Client client, String commandName, String description, CommandType commandType) {
        super(commandName, description, commandType);
        this.client = client;
    }

    /**
     * Выполнение команды
     * @param args аргументы
     * @return успешно ли выполнилась команда
     */
    public abstract boolean execute(String line, String[] args);

    public Client getClient() {
        return client;
    }

    public Console getConsole() {
        return client.getConsole();
    }
}
