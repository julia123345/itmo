package app.commands;

public abstract class Command {
    private String commandName;
    private String description;
    private CommandType commandType;

    public Command(String commandName, String description,
                   CommandType commandType) {
        this.commandName = commandName;
        this.description = description;
        this.commandType = commandType;
    }

    public String getDescription() {
        return description;
    }

    public String getCommandName() {
        return commandName;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}