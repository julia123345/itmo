package myapp.geometry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "results")
@NamedQueries({
        @NamedQuery(
                name = "Result.findByLogin",
                query = "SELECT r FROM Result r WHERE r.login = :login ORDER BY r.id"
        ),
        @NamedQuery(
                name = "Result.deleteByLogin",
                query = "DELETE FROM Result r WHERE r.login = :login"
        )
})
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String login;

    public double x;
    public double y;
    public double r;
    public boolean hit;
}

