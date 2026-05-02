package myapp.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import myapp.entity.User;
import myapp.persistence.PersistenceHolder;

import java.util.Optional;

@ApplicationScoped
public class UserDAO {

    @Inject
    private PersistenceHolder persistenceHolder;

    public Optional<User> findByLogin(String login) {
        if (login == null || login.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query =
                    em.createNamedQuery("User.findByLogin", User.class);
            query.setParameter("login", login.trim());

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        if (user == null) return;

        EntityManagerFactory emf = persistenceHolder.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (em.find(User.class, user.login) == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
