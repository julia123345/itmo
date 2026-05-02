package myapp.geometry.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import myapp.geometry.api.dto.CheckResponse;
import myapp.geometry.api.dto.PointRequest;
import myapp.geometry.entity.Result;
import myapp.geometry.persistence.PersistenceHolder;
import myapp.geometry.util.AreaChecker;

@ApplicationScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeometryResource {

    @Inject
    private PersistenceHolder persistenceHolder;

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok().build();
    }

    @POST
    @Path("/geometry/check")
    public Response checkPoint(PointRequest dto, @Context SecurityContext securityContext) {

        String login = (securityContext == null ||
                securityContext.getUserPrincipal() == null)
                ? null
                : securityContext.getUserPrincipal().getName();

        if (login == null || login.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Double x = dto == null ? null : dto.x();
        Double y = dto == null ? null : dto.y();
        Double r = dto == null ? null : dto.r();

        if (x == null || y == null || r == null || r <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean hit = AreaChecker.check(x, y, r);

        EntityManager em = persistenceHolder
                .getEntityManagerFactory()
                .createEntityManager();

        try {
            em.getTransaction().begin();

            Result res = new Result();
            res.login = login;
            res.x = x;
            res.y = y;
            res.r = r;
            res.hit = hit;

            em.persist(res);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return Response.ok(new CheckResponse(hit)).build();
    }
}