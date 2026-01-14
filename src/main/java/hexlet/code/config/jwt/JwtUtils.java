package hexlet.code.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import hexlet.code.exception.RsaKeyLoadingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
public class JwtUtils {

    @Value("${rsa.private-key:classpath:certs/private.pem}")
    private Resource privateKeyResource;

    @Value("${rsa.public-key:classpath:certs/public.pem}")
    private Resource publicKeyResource;

    @Value("${RSA_PRIVATE_KEY:}")
    private String privateKeyString;

    @Value("${RSA_PUBLIC_KEY:}")
    private String publicKeyString;

    @Value("${rsa.expiration:86400000}")
    private Long expiration;

    private Key privateKey;
    private Key publicKey;

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            if (privateKeyString != null && !privateKeyString.isEmpty()
                && publicKeyString != null && !publicKeyString.isEmpty()) {
                privateKey = loadPrivateKeyFromString(privateKeyString);
                publicKey = loadPublicKeyFromString(publicKeyString);
                log.info("RSA keys loaded from environment variables");
            } else if (privateKeyResource.exists() && publicKeyResource.exists()) {
                privateKey = loadPrivateKey(privateKeyResource);
                publicKey = loadPublicKey(publicKeyResource);
                log.info("RSA keys loaded from file resources");
            } else {
                log.warn("RSA keys not found, generating test keys");
                KeyPair keyPair = generateTestKeyPair();
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();
            }
        } catch (Exception e) {
            throw new RsaKeyLoadingException("Failed to load RSA keys", e);
        }
    }

    private KeyPair generateTestKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private Key loadPrivateKey(Resource resource)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = resource.getInputStream().readAllBytes();
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        return loadPrivateKeyFromString(key);
    }

    private Key loadPublicKey(Resource resource)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = resource.getInputStream().readAllBytes();
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        return loadPublicKeyFromString(key);
    }

    private Key loadPrivateKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private Key loadPublicKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
