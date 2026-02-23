package app;

import java.io.Serializable;

/**
 * Пользователь системы (логин и пароль),
 * передаётся между клиентом и сервером в сериализованном виде.
 */
public class User implements Serializable {

    private String login;
    private String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
