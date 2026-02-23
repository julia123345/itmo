package app.server;

import app.UserRequest;
import ru.bright.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * Модуль чтения запроса (ServerReadingHandler).
 * Возвращает Callable<UserRequest>, который выполняет неблокирующий read
 * и преобразует байты в объект UserRequest (через ObjectInputStream).
 *
 * Важное изменение: при ошибках чтения/разрыве соединения корректно закрываем канал и отменяем ключ,
 * и **никогда** не пытаемся отправлять ответ в уже разорванный канал.
 */
public class ServerReadingHandler {

    private final Server server;

    public ServerReadingHandler(Server server) {
        this.server = server;
    }

    protected Callable<UserRequest> handleReading(SelectionKey key) throws IOException {
        return () -> {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(100000);

            int bytesRead;
            try {
                bytesRead = clientChannel.read(buffer);
            } catch (IOException e) {
                // Клиент разорвал соединение или произошла другая ошибка — корректно закрываем соединение
                closeConnectionQuietly(key, clientChannel);
                server.getLogger().log(Level.INFO, "Client connection closed during read: " + e.getMessage());
                return null;
            }

            if (bytesRead == -1) {
                // Клиент закрыл соединение
                closeConnectionQuietly(key, clientChannel);
                server.getLogger().log(Level.INFO, "Client closed connection (read returned -1).");
                return null;
            }

            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {

                Object object = ois.readObject();
                if (!(object instanceof UserRequest)) {
                    // Некорректный формат запроса от клиента — отправляем ошибку только если канал валиден.
                    if (key != null && key.isValid()) {
                        server.sendError(key, "Wrong request format");
                    }
                    return null;
                }
                return (UserRequest) object;
            } catch (IOException | ClassNotFoundException e) {
                // Ошибка десериализации — клиент, возможно, закрылся во время передачи.
                server.getLogger().log(Level.SEVERE, "Error while deserializing object from client", e);
                // не пытаемся отвечать, если соединение разорвано — просто закрываем канал
                closeConnectionQuietly(key, clientChannel);
            }
            return null;
        };
    }

    private void closeConnectionQuietly(SelectionKey key, SocketChannel channel) {
        try {
            if (key != null) key.cancel();
        } catch (Exception ignored) {}
        try {
            if (channel != null && channel.isOpen()) channel.close();
        } catch (Exception ignored) {}
    }
}