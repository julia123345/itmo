package myapp.auth.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class PersistenceHolder {

    private EntityManagerFactory emf;

    @PostConstruct
    void init() {
        emf = Persistence.createEntityManagerFactory("authPU");
    }

    @PreDestroy
    void destroy() {
        if (emf != null) emf.close();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}

