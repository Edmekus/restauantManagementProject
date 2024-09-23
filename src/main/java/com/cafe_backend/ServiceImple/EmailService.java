package com.cafe_backend.ServiceImple;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("okmekus26@gmail.com"); // email expéditeur

        mailSender.send(message);
    }

    public void forgotMail(String to, String subject, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("okmekus26@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        // Créer le lien de réinitialisation de mot de passe
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // Créer le message HTML
        String htmlMsg = "<p><b>Vos données de connexion pour le système de gestion de restaurant</b></p>" +
                "<p><b>Email :</b> " + to + "</p>" +
                "<p><b>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</b></p>" +
                "<p><a href=\"" + resetLink + "\">Réinitialiser votre mot de passe</a></p>";

        message.setContent(htmlMsg, "text/html");
        mailSender.send(message);
    }






}
