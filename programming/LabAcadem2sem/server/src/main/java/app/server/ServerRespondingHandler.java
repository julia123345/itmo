package app.server;

import app.ServerMain;
import app.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

public class ServerRespondingHandler {

    private Server server;

    public ServerRespondingHandler(Server server) {
        this.server = server;
    }

    protected void responseToClient(SelectionKey key, Response response) {
        if (key == null || !key.isValid()) return;
        SocketChannel clientChannel = (SocketChannel) key.channel();
        if (clientChannel == null) return;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(response);
            }
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error while response to client", e);
            // корректно закрываем соединение клиента, но не завершаем сервер
            try {
                key.cancel();
            } catch (Exception ignored) {}
            try {
                if (clientChannel != null && clientChannel.isOpen()) clientChannel.close();
            } catch (Exception ignored) {}
        }
    }

    protected void responseToClient(SelectionKey key, SocketChannel clientChannel, Response response) {
        if (clientChannel == null) return;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(response);
            }
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (IOException e) {
            ServerMain.getLogger().log(Level.SEVERE, "Error while response to client", e);
            // если передали конкретный канал — просто попытаться закрыть его
            try {
                if (clientChannel != null && clientChannel.isOpen()) clientChannel.close();
            } catch (Exception ignored) {}
        }
    }
}