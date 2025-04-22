package com.recrutement.app.service;

import com.recrutement.app.model.*;
import com.recrutement.app.model.enums.StatutContact;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recrutement.app.dto.NotificationCandidatDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.*;

@Service
public class NotificationCandidatService {

    private final NotificationCandidatRepository notificationCandidatRepository;
    private final ContactCandidatRepository contactCandidatRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CompetenceCandidatRepository competenceCandidatRepository;
    private final CompetenceOffreRepository competenceOffreRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationCandidatService(
            NotificationCandidatRepository notificationCandidatRepository,
            ContactCandidatRepository contactCandidatRepository,
            CandidatRepository candidatRepository,
            OffreEmploiRepository offreEmploiRepository,
            CompetenceCandidatRepository competenceCandidatRepository,
            CompetenceOffreRepository competenceOffreRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationCandidatRepository = notificationCandidatRepository;
        this.contactCandidatRepository = contactCandidatRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.competenceCandidatRepository = competenceCandidatRepository;
        this.competenceOffreRepository = competenceOffreRepository;
        this.messagingTemplate = messagingTemplate;
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

        notification = notificationCandidatRepository.save(notification);
        
        // Notifier via WebSocket
        Long candidatId = notification.getCandidat().getId();
        NotificationCandidatDTO notificationDTO = convertToDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, List.of(notificationDTO));
        long unreadCount = getUnreadNotificationsCount(candidatId);
        messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, unreadCount);
    }

    public List<NotificationCandidat> getNotificationsAcceptation(Boolean estLue) {
        if (estLue != null) {
            return notificationCandidatRepository.findByTypeAndEstLue(TypeNotification.ACCEPTATION, estLue);
        }
        return notificationCandidatRepository.findByType(TypeNotification.ACCEPTATION);
    }

    public NotificationCandidat marquerCommeLue(Long notificationId) {
        NotificationCandidat notification = notificationCandidatRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée."));
        notification.setEstLue(true);
        notification = notificationCandidatRepository.save(notification);
        
        // Notifier via WebSocket
        Long candidatId = notification.getCandidat().getId();
        NotificationCandidatDTO notificationDTO = convertToDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, List.of(notificationDTO));
        long unreadCount = getUnreadNotificationsCount(candidatId);
        messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, unreadCount);
        
        return notification;
    }

    public List<NotificationCandidat> getNotificationsNonLues(Long candidatId) {
        return notificationCandidatRepository.findByCandidatIdAndEstLue(candidatId, false);
    }

    public List<NotificationCandidat> getAllNotifications(Long candidatId) {
        return notificationCandidatRepository.findByCandidatId(candidatId);
    }

    @Transactional
    public void marquerToutesCommeLues(Long candidatId) {
        notificationCandidatRepository.updateAllNotificationsAsRead(candidatId);
        
        // Notifier via WebSocket
        List<NotificationCandidatDTO> notifications = getNotificationsForCandidat(candidatId);
        messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notifications);
        messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, 0L);
    }

    @Transactional
    public NotificationCandidat acceptContactAndCreateNotification(Long contactId) {
        ContactCandidat contact = contactCandidatRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact non trouvé"));

        if (contact.getStatut() != StatutContact.DEMANDE) {
            throw new IllegalStateException("Le contact n'est pas en statut DEMANDE");
        }

        contact.setStatut(StatutContact.ACCEPTE);
        contact.setDateConnexion(LocalDate.now());
        contactCandidatRepository.save(contact);

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

        notification = notificationCandidatRepository.save(notification);
        
        // Notifier via WebSocket
        Long candidatId = notification.getCandidat().getId();
        NotificationCandidatDTO notificationDTO = convertToDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, List.of(notificationDTO));
        long unreadCount = getUnreadNotificationsCount(candidatId);
        messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, unreadCount);
        
        return notification;
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
                                cand.getCompetence().getId().equals(req.getCompetence().getId()) &&
                                        cand.getNiveau() >= req.getNiveau()))
                .count();

        return (double) matchCount / competencesRequises.size() * 100;
    }

    private Optional<NotificationCandidat> creerNotificationSiInexistante(Candidat candidat, OffreEmploi offre, double matchPercentage) {
        if (!notificationCandidatRepository.existsByCandidatAndItemReference(candidat, offre.getId().toString())) {
            NotificationCandidat notif = new NotificationCandidat();
            notif.setCandidat(candidat);
            notif.setType(TypeNotification.OFFRE);
            notif.setDateCreation(LocalDate.now());
            notif.setEstLue(false);
            notif.setItemReference(offre.getId().toString());
            notif.setContenu(String.format(
                    "Nouvelle offre correspondant à %.0f%% de vos compétences : %s",
                    matchPercentage,
                    offre.getTitre()
            ));
            
            notif = notificationCandidatRepository.save(notif);
            
            // Notifier via WebSocket pour la nouvelle notification d'offre
            Long candidatId = candidat.getId();
            NotificationCandidatDTO notificationDTO = convertToDTO(notif);
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, List.of(notificationDTO));
            long unreadCount = getUnreadNotificationsCount(candidatId);
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, unreadCount);
            
            return Optional.of(notif);
        }
        return Optional.empty();
    }

    @Transactional
    public List<NotificationCandidat> genererNotificationsMatchingPourOffre(Long offreId, int seuilMatching) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));

        List<CompetenceOffre> competencesOffre = competenceOffreRepository.findByOffreEmploiId(offreId);
        List<Candidat> tousCandidats = candidatRepository.findAll();
        List<NotificationCandidat> notificationsCreees = new ArrayList<>();

        for (Candidat candidat : tousCandidats) {
            List<CompetenceCandidat> competencesCandidat = competenceCandidatRepository.findByCandidatId(candidat.getId());

            double matching = calculerPourcentageMatching(competencesCandidat, competencesOffre);

            if (matching >= seuilMatching) {
                creerNotificationSiInexistante(candidat, offre, matching)
                        .ifPresent(notificationsCreees::add);
            }
        }

        return notificationsCreees;
    }

    // Méthodes pour WebSocket et DTO
    public List<NotificationCandidatDTO> getNotificationsForCandidat(Long candidatId) {
        List<NotificationCandidat> notifications = notificationCandidatRepository.findByCandidatId(candidatId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadNotificationsCount(Long candidatId) {
        return notificationCandidatRepository.countByCandidatIdAndEstLueFalse(candidatId);
    }

    public NotificationCandidat createNotification(Long candidatId, TypeNotification type, String contenu, String itemReference) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'ID: " + candidatId));
        
        NotificationCandidat notification = new NotificationCandidat();
        notification.setCandidat(candidat);
        notification.setType(type);
        notification.setContenu(contenu);
        notification.setItemReference(itemReference);
        notification.setEstLue(false);
        notification = notificationCandidatRepository.save(notification);
        
        // Envoyer notification via WebSocket
        NotificationCandidatDTO notificationDTO = convertToDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, List.of(notificationDTO));
        
        // Mettre à jour le compteur de notifications non lues
        long unreadCount = getUnreadNotificationsCount(candidatId);
        messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, unreadCount);
        
        return notification;
    }

    private NotificationCandidatDTO convertToDTO(NotificationCandidat notification) {
        NotificationCandidatDTO dto = new NotificationCandidatDTO();
        dto.setId(notification.getId());
        dto.setCandidatId(notification.getCandidat().getId());
        dto.setType(notification.getType());
        dto.setContenu(notification.getContenu());
        dto.setDateCreation(notification.getDateCreation());
        dto.setEstLue(notification.getEstLue());
        dto.setItemReference(notification.getItemReference());
        return dto;
    }
}