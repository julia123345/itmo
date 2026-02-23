package app.server;

import app.Response;
import app.ResponseStatus;
import app.User;
import app.UserRequest;
import app.commands.Command;
import app.commands.CommandType;
import app.commands.types.ServerObjectableCommand;

import java.nio.channels.SelectionKey;

public class ServerProcessHandler {

    private Server server;

    public ServerProcessHandler(Server server) {
        this.server = server;
    }

    protected void handleRequest(SelectionKey key, UserRequest request)  {
        User user = request.getUser();
        if (!(request.getCommandLine().trim().equalsIgnoreCase("auth") ||
                request.getCommandLine().trim().equalsIgnoreCase("register"))) {
            if (user != null && !server.getAuthManager().verify(user.getLogin(), user.getPassword())) {
                server.responseToClient(key, new Response(ResponseStatus.AUTH_FAILED, null));
                return;
            }
        }

        if (request.getAttachedObject() == null) {
            server.getServerCommandManager().executeCommand(user, key, request.getCommandLine());
        } else {
            String cmdName = request.getCommandLine().split(" ")[0];
            CommandType type;
            try {
                type = CommandType.valueOf(cmdName.toUpperCase());
            } catch (Exception e) {
                server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                return;
            }
            if (!server.getServerCommandManager().getCommandMap().containsKey(type)) {
                server.responseToClient(key, new Response(ResponseStatus.ERROR, "Wrong command"));
                return;
            }
            Command cmd = server.getServerCommandManager().getCommandMap().get(type);
            if (cmd instanceof ServerObjectableCommand) {
                server.getServerCommandManager().executeObjectableCommand(user, key, request.getCommandLine(), request.getAttachedObject());
            }
        }
    }
}
