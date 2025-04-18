package com.recrutement.app.service;

import com.recrutement.app.model.*;
import com.recrutement.app.model.enums.StatutContact;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import com.recrutement.app.model.CompetenceCandidat;
import com.recrutement.app.model.CompetenceOffre;

import jakarta.persistence.*;  

import jakarta.persistence.EntityNotFoundException;  

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NotificationCandidatService {

    private final NotificationCandidatRepository notificationCandidatRepository;
    private final ContactCandidatRepository contactCandidatRepository;
    private final CandidatRepository CandidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CompetenceCandidatRepository competenceCandidatRepository;
    private final CompetenceOffreRepository competenceOffreRepository;

    @Autowired
    public NotificationCandidatService(
            NotificationCandidatRepository notificationCandidatRepository,
            ContactCandidatRepository contactCandidatRepository,
            CandidatRepository CandidatRepository,
            OffreEmploiRepository offreEmploiRepository,
            CompetenceCandidatRepository competenceCandidatRepository,
            CompetenceOffreRepository competenceOffreRepository) {
        this.notificationCandidatRepository = notificationCandidatRepository;
        this.contactCandidatRepository = contactCandidatRepository;
        this.CandidatRepository = CandidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.competenceCandidatRepository = competenceCandidatRepository;
        this.competenceOffreRepository = competenceOffreRepository;
    }

    public void creerNotificationAcceptationContact(ContactCandidat contact) {
        if (contact == null || contact.getSender() == null || contact.getReceiver() == null) {
            throw new IllegalArgumentException("Les informations du contact sont incomplètes.");
        }

        NotificationCandidat notification = new NotificationCandidat();
        notification.setCandidat(contact.getSender());
        notification.setType(TypeNotification.ACCEPTATION);
        notification.setDateCreation(LocalDate.now());
        notification.setEstLue(false);
        notification.setItemReference(contact.getId().toString());

        String contenu = String.format(
            "Félicitations %s %s ! Votre demande de connexion avec %s a été acceptée.",
            contact.getSender().getPrenom(),
            contact.getSender().getNom(),
            contact.getReceiver().getNom()
        );
        notification.setContenu(contenu);

        notificationCandidatRepository.save(notification);
    }

    public List<NotificationCandidat> getNotificationsAcceptation(Boolean estLue) {
        try {
            if (estLue != null) {
                return notificationCandidatRepository.findByTypeAndEstLue(TypeNotification.ACCEPTATION, estLue);
            }
            return notificationCandidatRepository.findByType(TypeNotification.ACCEPTATION);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des notifications : " + e.getMessage());
        }
    }

    public NotificationCandidat marquerCommeLue(Long notificationId) {
        NotificationCandidat notification = notificationCandidatRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée."));
        notification.setEstLue(true);
        return notificationCandidatRepository.save(notification);
    }

    public List<NotificationCandidat> getNotificationsNonLues(Long candidatId) {
        if (candidatId == null) {
            throw new IllegalArgumentException("L'ID du candidat ne peut pas être null.");
        }
        return notificationCandidatRepository.findByCandidatIdAndEstLue(candidatId, false);
    }

    public List<NotificationCandidat> getAllNotifications(Long candidatId) {
        if (candidatId == null) {
            throw new IllegalArgumentException("L'ID du candidat ne peut pas être null.");
        }
        return notificationCandidatRepository.findByCandidatId(candidatId);
    }

    @Transactional
    public void marquerToutesCommeLues(Long candidatId) {
        if (candidatId == null) {
            throw new IllegalArgumentException("L'ID du candidat ne peut pas être null.");
        }
        notificationCandidatRepository.updateAllNotificationsAsRead(candidatId);
    }

    @Transactional
    public NotificationCandidat acceptContactAndCreateNotification(Long contactId) {
        // 1. Mettre à jour le statut du contact à ACCEPTÉ
        ContactCandidat contact = contactCandidatRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact non trouvé"));

        if (contact.getStatut() != StatutContact.DEMANDE) {
            throw new IllegalStateException("Le contact n'est pas en statut DEMANDE");
        }

        contact.setStatut(StatutContact.ACCEPTE);
        contact.setDateConnexion(LocalDate.now());
        contactCandidatRepository.save(contact);

        // 2. Créer une notification d'acceptation
        NotificationCandidat notification = new NotificationCandidat();
        notification.setCandidat(contact.getSender());
        notification.setType(TypeNotification.ACCEPTATION);
        notification.setDateCreation(LocalDate.now());
        notification.setEstLue(false);
        notification.setItemReference(contact.getId().toString());

        String contenu = String.format(
            "Félicitations %s %s ! Votre demande de connexion avec %s a été acceptée.",
            contact.getSender().getPrenom(),
            contact.getSender().getNom(),
            contact.getReceiver().getNom()
        );
        notification.setContenu(contenu);

        return notificationCandidatRepository.save(notification);
    }

  

   public List<NotificationCandidat> genererNotificationsOffreMatching(
        Long candidatId, Integer seuilMatching) {
    
    // 1. Vérifier l'existence du candidat
    Candidat candidat = CandidatRepository.findById(candidatId)
            .orElseThrow(() -> new EntityNotFoundException("Candidat non trouvé"));

    // 2. Récupérer les compétences du candidat
    List<CompetenceCandidat> competencesCandidat = competenceCandidatRepository.findByCandidatId(candidatId);
    
    // 3. Récupérer les offres actives
    List<OffreEmploi> offresActives = offreEmploiRepository.findByEstActiveTrue();
    
    List<NotificationCandidat> nouvellesNotifications = new ArrayList<>();

    // 4. Algorithme de matching
    for (OffreEmploi offre : offresActives) {
        List<CompetenceOffre> competencesRequises = competenceOffreRepository.findByOffreEmploiId(offre.getId());
        
        if (competencesRequises.isEmpty()) continue;
        
        double matchPercentage = calculerPourcentageMatching(competencesCandidat, competencesRequises);
        
        if (matchPercentage >= seuilMatching) {
            creerNotificationSiInexistante(candidat, offre, matchPercentage)
                .ifPresent(nouvellesNotifications::add);
        }
    }
    
    return nouvellesNotifications;
}

private Optional<NotificationCandidat> creerNotificationSiInexistante(
        Candidat candidat, OffreEmploi offre, double matchPercentage) {
    
    if (!notificationCandidatRepository.existsByCandidatAndItemReference(candidat, offre.getId().toString())) {
        NotificationCandidat notif = new NotificationCandidat();
        notif.setCandidat(candidat);
        notif.setType(TypeNotification.OFFRE);
        notif.setContenu(String.format(
            "Nouvelle offre correspondant à %.0f%% de vos compétences : %s",
            matchPercentage,
            offre.getTitre()));
        notif.setItemReference(offre.getId().toString());
        
        return Optional.of(notificationCandidatRepository.save(notif));
    }
    return Optional.empty();
}

public List<NotificationCandidat> getByCandidatId(Long candidatId) {
    return notificationCandidatRepository.findByCandidatId(candidatId);
}

 private double calculerPourcentageMatching(List<CompetenceCandidat> competencesCandidat, 
                                             List<CompetenceOffre> competencesRequises) {
        if (competencesRequises == null || competencesRequises.isEmpty()) {
            return 0.0;
        }

        long matchCount = competencesRequises.stream()
                .filter(req -> competencesCandidat.stream()
                        .anyMatch(cand -> 
                            cand.getCompetence().getId().equals(req.getCompetence().getId()) 
                            && cand.getNiveau() >= req.getNiveau()))
                .count();
        
        return (double) matchCount / competencesRequises.size() * 100;
    }
}
