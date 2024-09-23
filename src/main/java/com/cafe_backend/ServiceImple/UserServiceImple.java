package com.cafe_backend.ServiceImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Dao.UserInterface;
import com.cafe_backend.JWT.CustomerUsersDetailsService;
import com.cafe_backend.JWT.JwtFilter;
import com.cafe_backend.JWT.JwtUtil;
import com.cafe_backend.Models.User;
import com.cafe_backend.Service.UserService;
import com.cafe_backend.Util.RestaurantUtils;
import com.cafe_backend.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImple implements UserService {

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    // Déclarez un logger pour cette classe
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImple.class);
    @Autowired
    UserInterface userInterface;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userInterface.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    User newUser = getUserFromMap(requestMap);
                    newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Encoder le mot de passe
                    userInterface.save(newUser);
                    return RestaurantUtils.getResponseEntity("Enrégistré avec succès", HttpStatus.OK);
                } else {
                    return RestaurantUtils.getResponseEntity("L'Email existe déjà.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return RestaurantUtils.getResponseEntity(RestaurantContants.invalide_data, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            log.error("Une erreur s'est produite ", ex);
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password")); // Mot de passe non encodé ici
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> logIn(Map<String, String> requestMap) {
        log.info("inside logIn");

        try {
            // Authentifier l'utilisateur
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            // Vérifier si l'utilisateur est authentifié
            if (auth.isAuthenticated()) {
                // Obtenir les détails de l'utilisateur
                User user = customerUsersDetailsService.getUserDetail();

                // Vérifier le statut de l'utilisateur
                if (user.getStatus().equalsIgnoreCase("true")) {
                    // Générer le token
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
                    return new ResponseEntity<String>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"Attends l'approbation de l'Admin.\"}", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>("{\"message\":\"Attends l'approbation de l'Admin.\"}", HttpStatus.BAD_REQUEST);
            }
        } catch (UsernameNotFoundException e) {
            log.error("Utilisateur non trouvé", e);
            return new ResponseEntity<String>("{\"message\":\"Utilisateur non trouvé.\"}", HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            log.error("Mauvais mot de passe", e);
            return new ResponseEntity<String>("{\"message\":\"Mauvais mot de passe.\"}", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            log.error("Une erreur s'est produite", ex);
            return new ResponseEntity<String>("{\"message\":\"Mauvaises informations d'identification.\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            // Récupérer tous les utilisateurs depuis la base de données
            List<User> users = userInterface.findAll();

            // Convertir les utilisateurs en UserWrapper
            List<UserWrapper> userWrappers = users.stream()
                    .map(user -> new UserWrapper(user.getId(), user.getName(), user.getEmail(), user.getContactNumber(), user.getStatus()))
                    .collect(Collectors.toList());

            // Retourner les données des utilisateurs avec un code de statut OK
            return ResponseEntity.ok(userWrappers);
        } catch (Exception e) {
            // Enregistrer l'erreur
            logger.error("Une erreur s'est produite lors de la récupération des utilisateurs", e);

            // Retourner un message d'erreur avec un code de statut INTERNAL_SERVER_ERROR
            return new ResponseEntity<>(Collections.singletonList(new UserWrapper()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            String userEmail = requestMap.get("email");
            String newStatus = requestMap.get("status");

            if (userEmail == null || newStatus == null) {
                logger.warn("Données de demande invalides : email ou status manquant.");
                return RestaurantUtils.getResponseEntity("Données invalides", HttpStatus.BAD_REQUEST);
            }

            // Vérifiez les permissions ici
            User user = userInterface.findByEmail(userEmail);
            if (user == null) {
                logger.warn("Utilisateur non trouvé pour l'email : {}", userEmail);
                return RestaurantUtils.getResponseEntity("Utilisateur non trouvé", HttpStatus.UNAUTHORIZED);
            }

            if (!user.getRole().equals("admin")) {
                logger.warn("Accès non autorisé pour l'utilisateur : {}", userEmail);
                return RestaurantUtils.getResponseEntity("Accès non autorisé", HttpStatus.FORBIDDEN);
            }

            // Récupérer tous les administrateurs
            List<User> admins = userInterface.findAllByRole("admin");

            // Envoyer un email à tous les administrateurs
            if (admins != null && !admins.isEmpty() ) {
                for (User admin : admins) {
                    emailService.sendEmail(admin.getEmail(), "Mise à jour utilisateur",
                            "Une demande de mise à jour a été faite pour l'utilisateur avec l'email : " + userEmail);
                }
            } else {
                logger.warn("Aucun administrateur trouvé pour envoyer l'email.");
            }

            // Effectuer la mise à jour
            user.setStatus(String.valueOf(Boolean.parseBoolean(newStatus)));
            userInterface.save(user);

            return RestaurantUtils.getResponseEntity("Mise à jour réussie", HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur", ex);
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> ckecktoken() {
        return RestaurantUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            // Récupérer l'utilisateur actuellement connecté
            User userObj = userInterface.findByEmail(jwtFilter.getCurrentUser());

            if (userObj != null) {
                // Comparer l'ancien mot de passe encodé
                if (passwordEncoder.matches(requestMap.get("oldPassword"), userObj.getPassword())) {
                    // Encoder le nouveau mot de passe avant de le sauvegarder
                    userObj.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
                    userInterface.save(userObj);
                    return RestaurantUtils.getResponseEntity("Mot de passe modifié avec succès", HttpStatus.OK);
                }
                return RestaurantUtils.getResponseEntity("Ancien mot de passe incorrect", HttpStatus.BAD_REQUEST);
            }
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Erreur lors de la modification du mot de passe", ex);
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userInterface.findByEmail(requestMap.get("email"));
            if (user != null && !user.getEmail().isEmpty()) {
                // Générer un jeton de réinitialisation
                String resetToken = generateResetToken();
                user.setResetToken(resetToken);
                user.setTokenExpirationDate(LocalDateTime.now().plusHours(1)); // Expire dans 1 heure
                userInterface.save(user);

                // Envoyer l'e-mail avec le lien de réinitialisation
                String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
                emailService.forgotMail(user.getEmail(),
                        "Réinitialisation de votre mot de passe",
                        "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink);

                return RestaurantUtils.getResponseEntity("Vérifiez votre mail pour les instructions de réinitialisation.", HttpStatus.OK);
            } else {
                return RestaurantUtils.getResponseEntity("Utilisateur non trouvé.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            logger.error("Erreur lors de l'envoi de l'e-mail de réinitialisation", ex);
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        try {
            User user = userInterface.findByResetToken(token);
            if (user != null && user.getTokenExpirationDate().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword)); // Encoder le nouveau mot de passe
                user.setResetToken(null); // Réinitialiser le jeton
                user.setTokenExpirationDate(null); // Réinitialiser la date d'expiration
                userInterface.save(user);
                return RestaurantUtils.getResponseEntity("Mot de passe réinitialisé avec succès.", HttpStatus.OK);
            }
            return RestaurantUtils.getResponseEntity("Jeton invalide ou expiré.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("Erreur lors de la réinitialisation du mot de passe", ex);
            return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
