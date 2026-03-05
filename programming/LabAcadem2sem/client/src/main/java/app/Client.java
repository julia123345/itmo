package app;

import app.util.BasicConsole;
import app.util.ClientCommandManager;
import app.util.Console;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private final String host;
    private final int portNumber;
    private Console console;
    private SocketChannel clientChannel;
    private ClientCommandManager commandManager;
    private Selector selector;
    private BufferedReader consoleReader;
    private boolean isConnected;
    private boolean isAuthorized;
    private User user;
    private long lastReconnectTime = 0;
    private static final long RECONNECT_DELAY = 3000; // 3 секунды

    public Client(Console console, String host, int portNumber) {
        this.portNumber = portNumber;
        this.host = host;
        this.console = console;
        this.isConnected = false;
    }

    public boolean openConnection() {
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (clientChannel != null && clientChannel.isOpen()) {
                clientChannel.close();
            }

            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.connect(new InetSocketAddress(host, portNumber));

            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_CONNECT);

            return true;
        } catch (IOException e) {
            console.printErr("Connection error: " + e.getMessage());
            return false;
        }
    }

    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        console.println("Connected to server");
        channel.register(selector, SelectionKey.OP_READ);
        isConnected = true;
    }

    private void readFromServer(SelectionKey key) {
        SocketChannel ch = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        try {
            int bytesRead = ch.read(buffer);
            if (bytesRead == -1) {
                isConnected = false;
                key.cancel();
                ch.close();
                return;
            }
            if (bytesRead == 0) return;

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {

                Object object = ois.readObject();
                if (object instanceof Response) {
                    Response response = (Response) object;
                    switch (response.getStatus()) {
                        case OK -> console.println(response.getMessage());
                        case ERROR -> console.printErr("Server error: " + response.getMessage());
                        case AUTH_PASSED -> {
                            console.println(response.getMessage());
                            isAuthorized = true;
                        }
                        case AUTH_FAILED -> {
                            console.printErr("Auth failed");
                            isAuthorized = false;
                        }
                        default -> console.println(response.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            isConnected = false;
            try { key.cancel(); } catch (Exception ignored) {}
            try { ch.close(); } catch (Exception ignored) {}
        } catch (ClassNotFoundException e) {
            console.printErr("Error reading response");
        }
    }

    public void startListening() throws IOException {
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ((BasicConsole) console).setReader(consoleReader);

        while (true) {
            //Обработка ввода пользователя
            if (System.in.available() > 0) {
                String line = consoleReader.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }

                if (!isConnected) {
                    console.printErr("Not connected to server. Type 'connect' to reconnect");
                    continue;
                }

                String cmdName = line.split("\\s+")[0];
                boolean isAuthCmd = cmdName.equalsIgnoreCase("auth") ||
                        cmdName.equalsIgnoreCase("register");

                if (isAuthorized || isAuthCmd) {
                    if (isAuthorized && isAuthCmd) {
                        console.println("Already authorized");
                    } else {
                        commandManager.executeCommand(user, line);
                    }
                } else {
                    console.printErr("Please login first (auth/register)");
                }
            }

            long now = System.currentTimeMillis();
            if (!isConnected && now - lastReconnectTime > RECONNECT_DELAY) {
                lastReconnectTime = now;
                if (openConnection()) {
                    // console.println("Reconnecting...");
                }
            }

            // Обработка сетевых событий
            if (selector != null && selector.isOpen()) {
                try {
                    if (selector.selectNow() > 0) {
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();

                            if (!key.isValid()) continue;

                            if (key.isConnectable()) {
                                handleConnect(key);
                            }
                            if (key.isReadable()) {
                                readFromServer(key);
                            }
                        }
                    }
                } catch (IOException e) {
                    isConnected = false;
                }
            }

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

    public void requestToServer(UserRequest request) throws IOException {
        if (clientChannel == null || !clientChannel.isOpen() || !isConnected) {
            throw new IOException("Not connected to server");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(out)) {

            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);

            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        }
    }

    public void setUser(User user) { this.user = user; }
    public User getUser() { return user; }
    public Console getConsole() { return console; }
    public void setCommandManager(ClientCommandManager manager) { this.commandManager = manager; }
}