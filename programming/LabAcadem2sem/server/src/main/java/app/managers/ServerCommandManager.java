package app.managers;

import app.User;
import app.commands.CommandManager;
import app.commands.CommandType;
import app.server.Server;
import app.commands.types.ServerCommand;
import app.commands.types.ServerObjectableCommand;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class ServerCommandManager extends CommandManager {

    private Server server;

    public ServerCommandManager(Server server) {
        this.server = server;
    }

    public void saveFile() {
        if (server.getCollectionManager() != null) {
            server.getCollectionManager().saveToFile();
        }
    }

    public boolean executeCommand(User user, SelectionKey key, CommandType commandType, String[] args) {
        if(!getCommandMap().containsKey(commandType)) {
            server.sendError(key, "Команды " + commandType.name() + " не существует");
            return false;
        }
        if(((ServerCommand)getCommandMap().get(commandType)).execute(user, key,args)) {
            if (getCommandHistory().size() >= 13) {
                getCommandHistory().poll();
            }
            getCommandHistory().add(commandType.name().toLowerCase());
        }
        return true;
    }

    public boolean executeCommand(User user, SelectionKey key, String line) {
        if(line == null || line.isEmpty()) return false;
        String cmdName = line.split(" ")[0];
        CommandType type;
        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            server.sendError(key,"Команды " + cmdName + " не существует");
            return false;
        }
        executeCommand(user, key, type, Arrays.copyOfRange(line.split(" "),
                1,line.split(" ").length));
        return true;
    }

    public boolean executeObjectableCommand(User user, SelectionKey key, CommandType commandType, String[] args, Object object) {
        if(!getCommandMap().containsKey(commandType)) {
            server.sendError(key, "Команды " + commandType.name() + " не существует");
            return false;
        }
        if(((ServerObjectableCommand)getCommandMap().get(commandType)).execute(user, key,args, object)) {
            if (getCommandHistory().size() >= 13) {
                getCommandHistory().poll();
            }
            getCommandHistory().add(commandType.name().toLowerCase());
        }
        return true;
    }

    public boolean executeObjectableCommand(User user, SelectionKey key, String line, Object object) {
        if(line == null || line.isEmpty()) return false;
        String cmdName = line.split(" ")[0];
        CommandType type;
        try{
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (Exception e) {
            server.sendError(key,"Команды " + cmdName + " не существует");
            return false;
        }
        executeObjectableCommand(user, key, type, Arrays.copyOfRange(line.split(" "),
                1,line.split(" ").length), object);
        return true;
    }
}