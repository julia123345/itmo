package myapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "results")
@NamedQueries({
        @NamedQuery(
                name = "Result.findByUser",
                query = "SELECT r FROM Result r WHERE r.user.login = :login ORDER BY r.id"
        ),
        @NamedQuery(
                name = "Result.deleteByUser",
                query = "DELETE FROM Result r WHERE r.user.login = :login"
        )
})
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public double x;
    public double y;
    public double r;
    public boolean hit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_login")
    public User user;
}
