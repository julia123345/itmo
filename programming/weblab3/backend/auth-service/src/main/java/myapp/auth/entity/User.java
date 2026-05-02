package myapp.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(length = 50)
    public String login;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;
}

