package com.cafe_backend.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtUtil {

    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    // Chargement de la clé secrète depuis les variables d'environnement ou le fichier de configuration
    @Value("${jwt.secret}")
    private String secret;

    // Génération de la clé de signature à partir de la clé secrète
    private Key getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("La clé secrète JWT n'est pas configurée correctement.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            // Logger et retourner une valeur par défaut ou lancer une exception
            logger.warning("Impossible d'extraire les claims du token.");
            return null;
        }
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Gestion de l'erreur (par exemple, token invalide ou expiré)
            logger.severe("Erreur lors de l'extraction des claims : " + e.getMessage());
            return null;
        }
    }

    public String generateToken(String userName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 heures
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        if (expiration == null) {
            return true; // Considère le token comme expiré s'il est impossible d'extraire sa date d'expiration
        }
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUserName(token);
        return (userName != null && userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
