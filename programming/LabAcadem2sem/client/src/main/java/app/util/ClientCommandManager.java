package app.util;

import app.Client;
import app.User;
import app.UserRequest;
import app.commands.CommandManager;
import app.commands.CommandType;
import app.commands.types.ClientCommand;

import java.util.Arrays;

public class ClientCommandManager extends CommandManager {

    private Client client;

    public ClientCommandManager(Client client) {
        this.client = client;
    }

    public boolean executeCommand(User user, CommandType commandType, String line, String[] args) {

        if(!getCommandMap().containsKey(commandType)) {
            client.getConsole().printErr("Команды " + commandType.name() + " не существует");
            return false;
        }

        if(getCommandMap().get(commandType) == null) {
            try {
                client.requestToServer(new UserRequest(user, line, null));
            } catch (Exception e) {
                client.getConsole().printErr("Ошибка отправки команды на сервер: " + e.getMessage());
                return false;
            }
            return true;
        }

        try {
            if(((ClientCommand)getCommandMap().get(commandType)).execute(line, args)) {
                if (getCommandHistory().size() >= 13) {
                    getCommandHistory().poll();
                }
                getCommandHistory().add(commandType.name().toLowerCase());
            }
        } catch (Exception e) {
            client.getConsole().printErr("Ошибка выполнения команды: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean executeCommand(User user, String line) {
        if(line == null || line.isEmpty()) return false;

        String[] parts = line.split("\\s+");
        String cmdName = parts[0];
        CommandType type;

        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            client.getConsole().printErr("Команды " + cmdName + " не существует");
            return false;
        }

        String[] args = parts.length > 1 ?
                Arrays.copyOfRange(parts, 1, parts.length) :
                new String[0];

        return executeCommand(user, type, line, args);
    }
}