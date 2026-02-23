package app.commands;

import app.Client;
import app.UserRequest;
import app.commands.types.ClientCommand;
import app.model.Person;
import app.util.requests.PersonRequest;

import java.io.IOException;

public class RemoveLowerCommand extends ClientCommand {

    public RemoveLowerCommand(Client client) {
        super(client, "remove_lower", "Удаляет элементы коллекции, меньшие заданного", CommandType.REMOVE_LOWER);
    }

    @Override
    public boolean execute(String line, String[] arguments) {
        if (arguments.length != 0) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        Person person = (Person) new PersonRequest(getConsole()).create();
        if (person == null) return false;
        try {
            getClient().requestToServer(new UserRequest(getClient().getUser(), line, person));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
