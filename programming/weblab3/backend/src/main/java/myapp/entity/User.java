package myapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NamedQuery(
        name = "User.findByLogin",
        query = "SELECT u FROM User u WHERE u.login = :login"
)
public class User {

    @Id
    @Column(length = 50)
    public String login;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;
}


