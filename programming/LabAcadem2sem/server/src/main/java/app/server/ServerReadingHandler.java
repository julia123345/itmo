package app.server;

import app.UserRequest;
import app.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class ServerReadingHandler {

    private final Server server;

    public ServerReadingHandler(Server server) {
        this.server = server;
    }

    protected Callable<UserRequest> handleReading(SelectionKey key) {
        return () -> {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(65536); // 64KB

            int bytesRead;
            try {
                bytesRead = clientChannel.read(buffer);
            } catch (IOException e) {
                // Клиент разорвал соединение
                server.getLogger().log(Level.INFO, "Client disconnected: " + e.getMessage());
                closeConnection(key, clientChannel);
                return null;
            }

            if (bytesRead == -1) {
                // Клиент закрыл соединение
                server.getLogger().log(Level.INFO, "Client closed connection");
                closeConnection(key, clientChannel);
                return null;
            }

            if (bytesRead == 0) {
                return null; // Нет данных для чтения
            }

            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {

                Object object = ois.readObject();
                if (!(object instanceof UserRequest)) {
                    server.getLogger().log(Level.WARNING, "Wrong request format from client");
                    if (key != null && key.isValid()) {
                        server.sendError(key, "Wrong request format");
                    }
                    return null;
                }

                server.getLogger().log(Level.INFO, "Received request from client");
                return (UserRequest) object;

            } catch (IOException | ClassNotFoundException e) {
                server.getLogger().log(Level.SEVERE, "Error deserializing object", e);
                // Не закрываем соединение при ошибке десериализации
                return null;
            }
        };
    }

    private void closeConnection(SelectionKey key, SocketChannel channel) {
        try {
            if (key != null) {
                key.cancel();
            }
        } catch (Exception ignored) {}

        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (Exception ignored) {}
    }
}