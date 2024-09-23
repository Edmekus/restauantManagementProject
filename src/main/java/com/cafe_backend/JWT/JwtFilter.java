package com.cafe_backend.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    CustomerUsersDetailsService service;

    @Autowired
    JwtUtil jwtUtil;

    Claims claims = null;
    private String userName = null;

    // Déclarez un logger
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("Filtrage de la requête : " + request.getRequestURI());

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            logger.debug("En-tête d'autorisation trouvé avec le jeton : " + token);

            try {
                userName = jwtUtil.extractUserName(token);
                claims = jwtUtil.extractAllClaims(token);
                logger.debug("Nom d'utilisateur extrait du jeton : " + userName);
                logger.debug("Revendications extraites du jeton : " + claims);
            } catch (Exception e) {
                logger.error("Erreur lors de l'extraction des informations du jeton", e);
            }
        } else {
            logger.debug("L'en-tête d'autorisation est manquant ou ne commence pas par 'Bearer '");
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Aucune authentification trouvée dans le contexte de sécurité. Chargement des détails de l'utilisateur.");
            UserDetails userDetails = service.loadUserByUsername(userName);

            if (userDetails != null) {
                logger.debug("Détails de l'utilisateur chargés : " + userDetails.getUsername());
            } else {
                logger.warn("Les détails de l'utilisateur n'ont pas pu être chargés pour : " + userName);
            }

            if (jwtUtil.validateToken(token, userDetails)) {
                logger.debug("Le jeton est valide pour l'utilisateur : " + userName);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.debug("La validation du jeton a échoué pour l'utilisateur : " + userName);
            }
        }

        logger.debug("Poursuite de la chaîne de filtres");
        filterChain.doFilter(request, response);
    }

    // Méthode pour obtenir les revendications
    public Claims getClaims() {
        return claims;
    }

    public Boolean isAdmin() {
        if (claims != null) {
            logger.debug("Revendications disponibles : " + claims);
            String role = (String) claims.get("role");
            logger.debug("Rôle extrait : " + role);
            return "admin".equalsIgnoreCase(role);
        }
        logger.warn("Les revendications sont nulles ou manquantes.");
        return false;
    }

    public Boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser() {
        return userName;
    }
}
