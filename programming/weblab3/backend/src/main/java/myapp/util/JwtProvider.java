package myapp.util;

import io.jsonwebtoken.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class JwtProvider {
    private final String secret = "myapp-super-secure-secret-key-for-jwt-tokens-2025";
    private final long expirationMs = 3600000; // 1 час

    public String generateToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String validateTokenAndGetLogin(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return null; // токен истек
        } catch (JwtException | IllegalArgumentException e) {
            return null; // невалидный токен
        }
    }
}