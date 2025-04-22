package com.recrutement.app.controller;

import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.service.NotificationCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import com.recrutement.app.dto.MatchingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import com.recrutement.app.model.ContactCandidat;
 
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationCandidatController {
    @Autowired
    private NotificationCandidatService notificationCandidatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/acceptation")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsAcceptation(
        @RequestParam(required = false) Boolean estLue) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getNotificationsAcceptation(estLue);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationCandidat> marquerNotificationCommeLue(@PathVariable Long id) {
        try {
            NotificationCandidat notification = notificationCandidatService.marquerCommeLue(id);
            
            // Notifier via WebSocket du changement
            Long candidatId = notification.getCandidat().getId();
            
            // Envoyer la notification mise à jour
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            
            // Envoyer le nombre mis à jour de notifications non lues
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, nonLues.size());
            
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/unread/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsNonLues(@PathVariable Long candidatId) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getNotificationsNonLues(candidatId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/all/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getAllNotifications(@PathVariable Long candidatId) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getAllNotifications(candidatId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/read-all/{candidatId}")
    public ResponseEntity<String> marquerToutesCommeLues(@PathVariable Long candidatId) {
        try {
            notificationCandidatService.marquerToutesCommeLues(candidatId);
            
            // Notifier via WebSocket que toutes les notifications ont été lues
            // Envoyer la liste des notifications mise à jour
            List<NotificationCandidat> notifications = notificationCandidatService.getAllNotifications(candidatId);
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notifications);
            
            // Mettre à jour le compteur (qui sera maintenant 0)
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, 0);
            
            return ResponseEntity.ok("Toutes les notifications ont été marquées comme lues");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur serveur: " + e.getMessage());
        }
    }

     @PutMapping("/accept-contact/{contactId}")
    @Transactional
    public ResponseEntity<NotificationCandidat> acceptContactAndCreateNotification(@PathVariable Long contactId) {
        try {
            NotificationCandidat notification = notificationCandidatService.acceptContactAndCreateNotification(contactId);
            
            // Notifier via WebSocket de la nouvelle notification
            Long candidatId = notification.getCandidat().getId();
            
            // Envoyer la notification
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            
            // Envoyer le nombre mis à jour de notifications non lues
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, nonLues.size());
            
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
 
    // API GET existante (à conserver)
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsByCandidat(
            @PathVariable Long candidatId) {
        List<NotificationCandidat> notifications = notificationCandidatService.getByCandidatId(candidatId);
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/generer-offres-matching")
    public ResponseEntity<List<NotificationCandidat>> genererNotificationsMatching(
            @RequestParam Long offreId,
            @RequestParam(defaultValue = "100") int seuilMatching) {

        List<NotificationCandidat> notifications =
            notificationCandidatService.genererNotificationsMatchingPourOffre(offreId, seuilMatching);
        
        // Pour chaque notification, informer le candidat concerné
        for(NotificationCandidat notification : notifications) {
            Long candidatId = notification.getCandidat().getId();
            
            // Envoyer la notification
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            
            // Mettre à jour le compteur
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, nonLues.size());
        }

        return ResponseEntity.ok(notifications);
    }
    
    // Endpoints WebSocket
    
    /**
     * Endpoint WebSocket pour s'abonner aux notifications d'un candidat
     */
    @MessageMapping("/subscribe/{candidatId}")
    @SendTo("/topic/notifications/{candidatId}")
    public List<NotificationCandidat> subscribeToNotifications(@DestinationVariable Long candidatId) {
        return notificationCandidatService.getAllNotifications(candidatId);
    }
    
    /**
     * Endpoint WebSocket pour s'abonner au compteur de notifications non lues
     */
    @MessageMapping("/subscribe/count/{candidatId}")
    @SendTo("/topic/notifications/count/{candidatId}")
    public int subscribeToNotificationCount(@DestinationVariable Long candidatId) {
        List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
        return nonLues.size();
    }
}