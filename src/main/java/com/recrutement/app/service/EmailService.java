package com.recrutement.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.OffreEmploi;
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerEmail(String destinataire, String sujet, String message) {
        // Simulation de l'envoi d'un email (log)
        System.out.println("Simulation d'envoi d'email à: " + destinataire);
        System.out.println("Sujet: " + sujet);
        System.out.println("Message: " + message);

        // Création et configuration d'un objet SimpleMailMessage
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinataire);
        email.setSubject(sujet);
        email.setText(message);

        // Envoi de l'email
        mailSender.send(email);
    }
    public void envoyerEmailInvitation(String email, String token, String entrepriseName) {
        // Compose the email content
        String subject = "Invitation à rejoindre " + entrepriseName;
        String message = "Bonjour,\n\n" +
                "Vous avez été invité à rejoindre " + entrepriseName + " en tant qu'employé.\n" +
                "Veuillez utiliser ce token pour finaliser votre inscription : " + token + "\n\n" +
                "Cordialement,\n" +
                "L'équipe de recrutement";

        // Create the email message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        // Send the email
        mailSender.send(mailMessage);
    }

    /**
     * Envoyer un email pour une nouvelle offre en attente de validation
     */
    public void envoyerEmailNouvelleOffre(Employe destinataire, OffreEmploi offre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire.getEmail());
        message.setSubject("Nouvelle offre d'emploi à valider");
        message.setText(
                "Bonjour " + destinataire.getPrenom() + ",\n\n" +
                        "Une nouvelle offre d'emploi a été créée par " + offre.getCreateur().getPrenom() + " " +
                        offre.getCreateur().getNom() + " et nécessite votre validation.\n\n" +
                        "Titre: " + offre.getTitre() + "\n" +
                        "Type de contrat: " + offre.getTypeContrat() + "\n" +
                        "Localisation: " + offre.getLocalisation() + "\n\n" +
                        "Veuillez vous connecter à la plateforme pour examiner cette offre.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe de recrutement"
        );

        mailSender.send(message);
    }

    /**
     * Envoyer un email pour une offre validée
     */
    public void envoyerEmailOffreValidee(Employe destinataire, OffreEmploi offre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire.getEmail());
        message.setSubject("Votre offre d'emploi a été validée");
        message.setText(
                "Bonjour " + destinataire.getPrenom() + ",\n\n" +
                        "Votre offre d'emploi '" + offre.getTitre() + "' a été validée par " +
                        offre.getValidateur().getPrenom() + " " + offre.getValidateur().getNom() + ".\n\n" +
                        "Elle est maintenant publiée et visible pour les candidats.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe de recrutement"
        );

        mailSender.send(message);
    }

    /**
     * Envoyer un email pour une offre rejetée
     */
    public void envoyerEmailOffreRejetee(Employe destinataire, OffreEmploi offre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire.getEmail());
        message.setSubject("Votre offre d'emploi a été rejetée");
        message.setText(
                "Bonjour " + destinataire.getPrenom() + ",\n\n" +
                        "Votre offre d'emploi '" + offre.getTitre() + "' a été rejetée par " +
                        offre.getValidateur().getPrenom() + " " + offre.getValidateur().getNom() + ".\n\n" +
                        "Motif: " + offre.getMotifRefus() + "\n\n" +
                        "Veuillez vous connecter à la plateforme pour modifier votre offre et la soumettre à nouveau.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe de recrutement"
        );

        mailSender.send(message);
    }

    /**
     * Envoyer un email pour une modification d'offre nécessitant validation
     */
    public void envoyerEmailModificationOffre(Employe destinataire, OffreEmploi offre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire.getEmail());
        message.setSubject("Offre d'emploi modifiée à valider");
        message.setText(
                "Bonjour " + destinataire.getPrenom() + ",\n\n" +
                        "L'offre d'emploi '" + offre.getTitre() + "' a été modifiée par " +
                        offre.getCreateur().getPrenom() + " " + offre.getCreateur().getNom() +
                        " et nécessite votre validation.\n\n" +
                        "Veuillez vous connecter à la plateforme pour examiner ces modifications.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe de recrutement"
        );

        mailSender.send(message);
    }
}
