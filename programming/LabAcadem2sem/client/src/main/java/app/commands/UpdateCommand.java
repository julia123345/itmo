package app.commands;

import app.Client;
import app.UserRequest;
import app.commands.types.ClientCommand;
import app.model.Person;
import app.util.requests.PersonRequest;

import java.io.IOException;

public class UpdateCommand extends ClientCommand {

    public UpdateCommand(Client client) {
        super(client, "update {id}", "Обновляет элемент в коллекции", CommandType.UPDATE);
    }

    @Override
    public boolean execute(String line, String[] arguments) {
        if (arguments.length != 1) {
            getConsole().println("Неверное использование команды.");
            return false;
        }
        try {
            Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            getConsole().printErr("ID должно быть целым числом");
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
