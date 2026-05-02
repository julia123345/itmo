package myapp.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import myapp.entity.Result;
import myapp.persistence.PersistenceHolder;

import java.util.List;

@ApplicationScoped
public class ResultDAO {

    @Inject
    private PersistenceHolder persistenceHolder;

    public void save(Result result) {
        if (result == null) return;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(result);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Result> findByUser(String login) {
        if (login == null || login.isBlank()) return List.of();

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Result> query =
                    em.createNamedQuery("Result.findByUser", Result.class);
            query.setParameter("login", login.trim());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public int deleteByUser(String login) {
        if (login == null || login.isBlank()) return 0;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            int n = em.createNamedQuery("Result.deleteByUser")
                    .setParameter("login", login.trim())
                    .executeUpdate();
            em.getTransaction().commit();
            return n;
        } finally {
            em.close();
        }
    }
}
