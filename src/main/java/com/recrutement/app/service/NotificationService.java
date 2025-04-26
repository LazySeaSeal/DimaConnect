package com.recrutement.app.service;

import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.NotificationCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NotificationService {

    @Autowired
    private NotificationCandidatRepository notificationCandidatRepository;

    /**
     * Créer une notification pour un candidat
     */
    public NotificationCandidat creerNotificationCandidat(
            Candidat candidat,
            TypeNotification type,
            String contenu,
            String itemReference) {

        NotificationCandidat notification = new NotificationCandidat();
        notification.setCandidat(candidat);
        notification.setType(type);
        notification.setContenu(contenu);
        notification.setDateCreation(LocalDate.now());
        notification.setEstLue(false);
        notification.setItemReference(itemReference);

        return notificationCandidatRepository.save(notification);
    }
}