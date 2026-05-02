package myapp.ws;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import myapp.dto.ResultDTO;
import myapp.entity.Result;
import myapp.entity.User;
import myapp.persistence.PersistenceHolder;
import myapp.util.AreaChecker;
import myapp.util.JwtProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@WebService(
        serviceName = "AreaWebService",
        portName = "AreaWebServicePort",
        targetNamespace = "http://myapp.ws/"
)
public class AreaWebService {

    @Resource
    private WebServiceContext wsContext;

    @Inject
    private PersistenceHolder persistenceHolder;

    @Inject
    private JwtProvider jwtProvider;

    private String getAuthorizedLogin() {
        MessageContext mc = wsContext.getMessageContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) mc.get(MessageContext.HTTP_REQUEST_HEADERS);

        if (headers != null) {
            List<String> authHeader = headers.get("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                String token = authHeader.get(0).replace("Bearer ", "");
                return jwtProvider.validateTokenAndGetLogin(token);
            }
        }
        return null;
    }

    @WebMethod
    public boolean checkPoint(double x, double y, double r) {
        String login = getAuthorizedLogin();
        if (login == null) return false;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            User user = em.find(User.class, login);
            if (user == null) {
                em.getTransaction().rollback();
                return false;
            }

            boolean hit = AreaChecker.check(x, y, r);

            Result res = new Result();
            res.x = x;
            res.y = y;
            res.r = r;
            res.hit = hit;
            res.user = user;

            em.persist(res);
            em.getTransaction().commit();
            return hit;
        } finally {
            em.close();
        }
    }

    @WebMethod
    public List<ResultDTO> getResults() {
        String login = getAuthorizedLogin();
        if (login == null) return List.of();

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Result> query =
                    em.createNamedQuery("Result.findByUser", Result.class);
            query.setParameter("login", login);

            return query.getResultList()
                    .stream()
                    .map(r -> new ResultDTO(
                            r.id, r.y, r.x, r.r, r.hit
                    ))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @WebMethod
    public int clearResults() {
        String login = getAuthorizedLogin();
        if (login == null) return 0;

        EntityManager em =
                persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            int n = em.createNamedQuery("Result.deleteByUser")
                    .setParameter("login", login)
                    .executeUpdate();
            em.getTransaction().commit();
            return n;
        } finally {
            em.close();
        }
    }
}