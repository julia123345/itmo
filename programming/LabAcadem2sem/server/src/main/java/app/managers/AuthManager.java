package app.managers;

import app.server.Server;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Аутентификация по файлу users (логин -> хэш пароля). Без БД.
 */
public class AuthManager {

    private final Server server;
    private final Map<String, String> users = new HashMap<>();
    private final String usersFilePath;

    public AuthManager(Server server, String usersFilePath) {
        this.server = server;
        this.usersFilePath = usersFilePath;
    }

    public void init() {
        Path path = Path.of(usersFilePath);
        if (!Files.exists(path)) {
            server.getLogger().log(Level.INFO, "Users file not found, starting with empty users");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(path)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int i = line.indexOf(' ');
                if (i <= 0) continue;
                String login = line.substring(0, i).trim();
                String hash = line.substring(i + 1).trim();
                users.put(login, hash);
            }
            server.getLogger().log(Level.INFO, "Users loaded from file");
        } catch (IOException e) {
            server.getLogger().log(Level.SEVERE, "Error loading users", e);
        }
    }

    private void saveUsers() {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(usersFilePath))) {
            for (Map.Entry<String, String> e : users.entrySet()) {
                w.write(e.getKey() + " " + e.getValue());
                w.newLine();
            }
        } catch (IOException e) {
            server.getLogger().log(Level.SEVERE, "Error saving users", e);
        }
    }

    public boolean hasLogin(String login) {
        return users.containsKey(login);
    }

    public boolean register(String login, String password) {
        String hash = hashPassword(password);
        if (hash == null || login == null || login.trim().isEmpty()) return false;
        if (users.containsKey(login)) return false;
        users.put(login.trim(), hash);
        saveUsers();
        return true;
    }

    public boolean verify(String login, String password) {
        String hash = hashPassword(password);
        return hash != null && users.containsKey(login) && users.get(login).equals(hash);
    }

    private String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] digest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, digest);
            String hex = no.toString(16);
            while (hex.length() < 32) hex = "0" + hex;
            return hex;
        } catch (NoSuchAlgorithmException e) {
            server.getLogger().log(Level.SEVERE, "MD2 not found", e);
            return null;
        }
    }
}
