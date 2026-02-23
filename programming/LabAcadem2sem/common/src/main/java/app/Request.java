package app;

import java.io.Serializable;

/**
 * Базовый сериализуемый запрос от клиента к серверу.
 * Содержит строку команды и (опционально) прикреплённый объект.
 */
public class Request implements Serializable {

    private String commandLine;
    private Object attachedObject = null;

    public Request(String commandLine, Object attachedObject) {
        this.commandLine = commandLine;
        this.attachedObject = attachedObject;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public Object getAttachedObject() {
        return attachedObject;
    }
}
