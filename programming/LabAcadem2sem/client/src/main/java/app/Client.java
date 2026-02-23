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

/**
 * Клиентское приложение.
 * Отвечает за подключение к серверу по неблокирующему {@link SocketChannel},
 * отправку сериализованных команд и приём/обработку ответов.
 */
public class Client {

    private final String host;
    private final int portNumber;
    private Console console;
    private SocketChannel clientChannel;
    private ClientCommandManager commandManager;
    private Selector selector;
    private BufferedReader consoleReader;
    private boolean isAvailable;
    private String lastCommand;
    private boolean isAuthorized;
    private User user;

    public Client(Console console, String host, int portNumber) {
        this.portNumber = portNumber;
        this.host = host;
        this.console = console;
        this.clientChannel = null;
        this.isAvailable = false;
    }

    public boolean openConnection() throws IOException {
        selector = null;
        try {
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.connect(new InetSocketAddress(host, portNumber));
            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            isAvailable = true;
            return true;
        } catch (Exception e) {
            console.printErr("Connection error: " + e.getMessage());
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            isAvailable = false;
            return false;
        }
    }

    public void startListening() throws IOException {
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ((BasicConsole)console).setReader(consoleReader);
        while (true) {
            if (consoleReader.ready()) {
                String line = consoleReader.readLine();
                if (line == null) break;
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equals("exit")) {
                    consoleReader.close();
                    System.exit(0);
                }
                lastCommand = line;
                if (!isAvailable) {
                    openConnection();
                    continue;
                }
                if (isAuthorized || (line.trim().equalsIgnoreCase("auth") || line.trim().equalsIgnoreCase("register"))) {
                    if (isAuthorized && (line.trim().equalsIgnoreCase("auth") || line.trim().equalsIgnoreCase("register"))) {
                        console.println("Вы уже авторизованы");
                    } else {
                        commandManager.executeCommand(user, line);
                    }
                } else {
                    console.printErr("Access error. Enter to system or register. (Commands: auth/register)");
                }
            }
            if (!isAvailable) {
                // немного отдыхаем и пробуем заново
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                continue;
            }
            try {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (!selectionKey.isValid()) continue;
                    if (selectionKey.isConnectable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }
                        console.println("Connected to " + channel.getRemoteAddress());
                        console.println("You can enter to system or register (Commands: auth/register)");
                        channel.register(selector, SelectionKey.OP_READ);
                        if (isAuthorized && lastCommand != null && !lastCommand.isEmpty()) {
                            commandManager.executeCommand(user, lastCommand);
                        }
                    }
                    if (selectionKey.isReadable()) {
                        readFromServer(selectionKey);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                console.printErr("Сервер временно недоступен. Попробуйте позже");
                if (selector != null) try { selector.close(); } catch (IOException ignored) {}
                isAvailable = false;
            }
        }
    }

    private ResponseStatus readFromServer(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        ResponseStatus status = null;
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int bytesRead;
        try {
            bytesRead = ch.read(buffer);
        } catch (IOException e) {
            isAvailable = false;
            key.cancel();
            try { ch.close(); } catch (IOException ignored) {}
            return null;
        }
        if (bytesRead == -1) {
            isAvailable = false;
            key.cancel();
            try { ch.close(); } catch (IOException ignored) {}
            return null;
        }
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            while (bais.available() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Object object = ois.readObject();
                    if (!(object instanceof Response)) continue;
                    Response response = (Response) object;
                    status = response.getStatus();
                    if (response.getStatus() == ResponseStatus.OK) {
                        console.println(response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.ERROR) {
                        console.printErr("Server error: " + response.getMessage());
                    } else if (response.getStatus() == ResponseStatus.DENIED) {
                        console.printErr("Command denied: " + response.getMessage());
                    } else if (status == ResponseStatus.AUTH_PASSED) {
                        if (response.getMessage() != null) {
                            console.println(response.getMessage());
                        }
                        isAuthorized = true;
                    } else if (status == ResponseStatus.AUTH_FAILED) {
                        console.printErr("Access error. Enter to system or register. (Commands: auth/register)");
                        isAuthorized = false;
                    }
                } catch (EOFException e) {
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    console.printErr("Error while reading response: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            console.printErr("Error while reading response: " + e.getMessage());
        }
        buffer.clear();
        return status;
    }

    public void requestToServer(UserRequest request) throws IOException {
        if (clientChannel == null || !clientChannel.isOpen()) throw new IOException("Client channel is closed");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(request);
            byte[] data = out.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(data);
            while (buf.hasRemaining()) {
                clientChannel.write(buf);
            }
        } catch (Exception e) {
            console.printErr("Error while writing request: " + e.getMessage());
            throw new IOException(e);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Console getConsole() {
        return console;
    }

    public void setCommandManager(ClientCommandManager clientCommandManager) {
        this.commandManager = clientCommandManager;
    }
}