package myapp.auth.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@ApplicationScoped
public class JwtProvider {

    private static final String DEFAULT_SECRET = "change-me-in-docker-compose-jwt-secret";
    private static final long DEFAULT_EXPIRATION_MS = 3600_000; // 1 hour

    private String secret() {
        String env = System.getenv("JWT_SECRET");
        return (env == null || env.isBlank()) ? DEFAULT_SECRET : env;
    }

    private long expirationMs() {
        String env = System.getenv("JWT_EXPIRATION_MS");
        if (env == null || env.isBlank()) return DEFAULT_EXPIRATION_MS;
        try {
            long v = Long.parseLong(env.trim());
            return v > 0 ? v : DEFAULT_EXPIRATION_MS;
        } catch (NumberFormatException e) {
            return DEFAULT_EXPIRATION_MS;
        }
    }

    public String generateToken(String login) {
        String subject = login == null ? "" : login.trim();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs()))
                .signWith(SignatureAlgorithm.HS256, secret().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String validateTokenAndGetLogin(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            return Jwts.parser()
                    .setSigningKey(secret().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}

