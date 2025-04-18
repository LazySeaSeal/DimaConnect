package com.recrutement.app.service;

import com.recrutement.app.model.ContactCandidat;
import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.NotificationCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationCandidatService {
    @Autowired
    private NotificationCandidatRepository notificationCandidatRepository;

    public void creerNotificationAcceptationContact(ContactCandidat contact) {
        if (contact == null || contact.getSender() == null || contact.getReceiver() == null) {
            throw new IllegalArgumentException("Les informations du contact sont incomplètes.");
        }
        
        NotificationCandidat notification = new NotificationCandidat();
        notification.setCandidat(contact.getSender()); // Using getSender() instead of getCandidat()
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
        notificationCandidatRepository.marquerToutesCommeLues(candidatId);
    }
}