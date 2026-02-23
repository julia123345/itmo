package app.server;

import app.Response;
import app.ResponseStatus;
import app.UserRequest;
import ru.bright.*;
import app.managers.AuthManager;
import app.managers.CollectionManager;
import app.managers.ServerCommandManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сервер: однопоточный цикл (selector).
 * Многопоточность: Fixed thread pool — чтение запросов; new Thread — обработка; ForkJoinPool — отправка ответов.
 */
public class Server {

    private final int portNumber;
    private final ServerConnectionHandler serverConnectionHandler;
    private final ServerReadingHandler serverReadingHandler;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ServerCommandManager serverCommandManager;
    private final ServerRespondingHandler serverRespondingHandler;
    private final ServerProcessHandler serverProcessHandler;
    private Logger logger;
    private CollectionManager collectionManager;
    private AuthManager authManager;

    /** Fixed thread pool для многопоточного чтения запросов */
    private final ExecutorService requestReaderPool;
    /** ForkJoinPool для многопоточной отправки ответов */
    private final ForkJoinPool responseSenderPool;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        this.serverConnectionHandler = new ServerConnectionHandler(this);
        this.serverReadingHandler = new ServerReadingHandler(this);
        this.serverRespondingHandler = new ServerRespondingHandler(this);
        this.serverProcessHandler = new ServerProcessHandler(this);
        this.requestReaderPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.responseSenderPool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism());
    }

    public void startServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(portNumber));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.log(Level.INFO, "Server listening on port: " + portNumber);
            while (true) {
                try {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            serverConnectionHandler.handleNewConnection(key, serverChannel, selector);
                        }
                        if (key.isReadable()) {
                            try {
                                scheduleReading(key);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Error while client reading", e.getMessage());
                                key.cancel();
                            }
                        }
                    }
                } catch (CancelledKeyException ignored) {}
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error working server socket", e.getMessage());
        } finally {
            try {
                if (serverChannel != null && serverChannel.isOpen()) serverChannel.close();
                if (selector != null && selector.isOpen()) selector.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing server socket");
            }
        }
    }

    /** Fixed thread pool — чтение; new Thread — обработка; ForkJoinPool — отправка. */
    private void scheduleReading(SelectionKey key) throws IOException {
        Callable<UserRequest> readTask = serverReadingHandler.handleReading(key);
        if (readTask == null) return;
        Future<UserRequest> future = requestReaderPool.submit(readTask);
        Thread processThread = new Thread(() -> {
            UserRequest req;
            try {
                req = future.get();
            } catch (Exception e) {
                // если не удалось прочитать — отправляем ответ (если ключ валиден)
                responseToClient(key, new Response(ResponseStatus.ERROR, "Exception: " + e.getMessage()));
                return;
            }
            if (req != null) {
                serverProcessHandler.handleRequest(key, req);
            }
        });
        processThread.start();
    }

    /** Отправка ответа через ForkJoinPool (с проверками) */
    public void responseToClient(SelectionKey key, Response response) {
        if (key == null || !key.isValid()) return;
        try {
            responseSenderPool.submit(() ->
                    serverRespondingHandler.responseToClient(key, response));
        } catch (RejectedExecutionException e) {
            // пул закрыт или перегружен — логируем и игнорируем
            logger.log(Level.WARNING, "Response pool rejected task: " + e.getMessage());
        }
    }

    public void responseToClient(SelectionKey key, java.nio.channels.SocketChannel clientChannel, Response response) {
        // если мы отправляем конкретному SocketChannel, проверяем канал
        if (clientChannel == null || !clientChannel.isOpen()) return;
        try {
            responseSenderPool.submit(() ->
                    serverRespondingHandler.responseToClient(key, clientChannel, response));
        } catch (RejectedExecutionException e) {
            logger.log(Level.WARNING, "Response pool rejected task: " + e.getMessage());
        }
    }

    public void shutdown() {
        getServerCommandManager().saveFile();
        try {
            if (serverChannel != null && serverChannel.isOpen()) serverChannel.close();
            if (selector != null && selector.isOpen()) selector.close();
            logger.log(Level.INFO, "Server is shutting down");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while shutting down server", e);
        } finally {
            requestReaderPool.shutdown();
            responseSenderPool.shutdown();
            System.exit(0);
        }
    }

    public AuthManager getAuthManager() { return authManager; }
    public void setAuthManager(AuthManager authManager) { this.authManager = authManager; }
    public void sendError(SelectionKey key, String message) {
        responseToClient(key, new Response(ResponseStatus.ERROR, message));
    }
    public void sendOK(SelectionKey key, String message) {
        responseToClient(key, new Response(ResponseStatus.OK, message));
    }
    public void sendAnother(SelectionKey key, ResponseStatus status, String message) {
        responseToClient(key, new Response(status, message));
    }
    public ServerConnectionHandler getServerConnectionHandler() { return serverConnectionHandler; }
    public ServerReadingHandler getServerReadingHandler() { return serverReadingHandler; }
    public CollectionManager getCollectionManager() { return collectionManager; }
    public void setCollectionManager(CollectionManager collectionManager) { this.collectionManager = collectionManager; }
    public void setServerCommandManager(ServerCommandManager serverCommandManager) { this.serverCommandManager = serverCommandManager; }
    public ServerCommandManager getServerCommandManager() { return serverCommandManager; }
    public void setLogger(Logger logger) { this.logger = logger; }
    public Logger getLogger() { return logger; }
}