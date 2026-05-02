package myapp.auth.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import myapp.auth.api.dto.CredentialsDTO;
import myapp.auth.api.dto.RegisterResponse;
import myapp.auth.api.dto.TokenResponse;
import myapp.auth.entity.User;
import myapp.auth.persistence.PersistenceHolder;
import myapp.auth.util.JwtProvider;
import myapp.auth.util.PasswordHasher;

@ApplicationScoped
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private PersistenceHolder persistenceHolder;

    @Inject
    private JwtProvider jwtProvider;

    @POST
    @Path("/register")
    public Response register(CredentialsDTO dto) {
        String login = dto == null || dto.login() == null ? null : dto.login().trim();
        String password = dto == null ? null : dto.password();

        if (login == null || login.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RegisterResponse(false)).build();
        }

        EntityManager em = persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            User existing = em.find(User.class, login);
            if (existing != null) {
                em.getTransaction().rollback();
                return Response.status(Response.Status.CONFLICT).entity(new RegisterResponse(false)).build();
            }

            User u = new User();
            u.login = login;
            u.passwordHash = PasswordHasher.hash(password);
            em.persist(u);

            em.getTransaction().commit();
            return Response.ok(new RegisterResponse(true)).build();
        } finally {
            em.close();
        }
    }

    @POST
    @Path("/login")
    public Response login(CredentialsDTO dto) {
        String login = dto == null || dto.login() == null ? null : dto.login().trim();
        String password = dto == null ? null : dto.password();

        if (login == null || login.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        EntityManager em = persistenceHolder.getEntityManagerFactory().createEntityManager();
        try {
            User user = em.find(User.class, login);
            if (user == null) return Response.status(Response.Status.UNAUTHORIZED).build();

            boolean ok = PasswordHasher.check(password, user.passwordHash);
            if (!ok) return Response.status(Response.Status.UNAUTHORIZED).build();

            return Response.ok(new TokenResponse(jwtProvider.generateToken(login))).build();
        } finally {
            em.close();
        }
    }
}

