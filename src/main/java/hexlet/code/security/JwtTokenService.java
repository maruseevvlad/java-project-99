package hexlet.code.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final SecretKey signingKey;

    public JwtTokenService(@Value("${jwt.secret:very-secret-key}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .signWith(signingKey)
                .compressWith(Jwts.ZIP.GZIP)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
