package myapp.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.persistence.EntityManager;
import myapp.entity.User;
import myapp.persistence.PersistenceHolder;
import myapp.util.JwtProvider;
import myapp.util.PasswordHasher;

@ApplicationScoped
@WebService(
        serviceName = "AuthWebService",
        portName = "AuthWebServicePort",
        targetNamespace = "http://myapp.ws/"
)
public class AuthWebService {

    @Inject
    private PersistenceHolder persistenceHolder;

    @Inject
    private JwtProvider jwtProvider;

    @WebMethod
    public String login(String login, String password) {
        if (login == null || login.isBlank()) return null;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            User user = em.find(User.class, login.trim());
            if (user == null) return null;

            boolean valid = PasswordHasher.check(
                    password == null ? "" : password,
                    user.passwordHash
            );

            if (valid) {
                return jwtProvider.generateToken(login.trim());
            }
            return null;
        } finally {
            em.close();
        }
    }

    @WebMethod
    public boolean register(String login, String password) {
        if (login == null || login.isBlank()) return false;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            if (em.find(User.class, login.trim()) != null) {
                em.getTransaction().rollback();
                return false;
            }

            User user = new User();
            user.login = login.trim();
            user.passwordHash = PasswordHasher.hash(password == null ? "" : password);

            em.persist(user);
            em.getTransaction().commit();
            return true;
        } finally {
            em.close();
        }
    }
}