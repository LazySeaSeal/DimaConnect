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
@RequestMapping("/api/candidat/notifications")
@Tag(name = "Notifications Candidat", description = "API pour gérer les notifications des candidats")
public class NotificationCandidatController {
    @Autowired
    private NotificationCandidatService notificationCandidatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Obtenir les notifications d'acceptation", 
               description = "Récupère les notifications d'acceptation, filtrées par statut de lecture si spécifié")
    @ApiResponse(responseCode = "200", description = "Notifications récupérées avec succès")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    @GetMapping("/acceptation")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsAcceptation(
        @Parameter(description = "Filtre par statut de lecture (optionnel)")
        @RequestParam(required = false) Boolean estLue) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getNotificationsAcceptation(estLue);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Marquer une notification comme lue", 
               description = "Change le statut d'une notification spécifique à 'lue'")
    @ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    @ApiResponse(responseCode = "404", description = "Notification non trouvée")
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationCandidat> marquerNotificationCommeLue(
        @Parameter(description = "ID de la notification", required = true)
        @PathVariable Long id) {
        try {
            NotificationCandidat notification = notificationCandidatService.marquerCommeLue(id);
            
            // Notifier via WebSocket du changement
            Long candidatId = notification.getCandidat().getId();
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            // 1. Format original
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            
            // 2. Format user/queue pour compatibilité supplémentaire
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications",
                notification
            );
            
            // Envoyer le nombre mis à jour de notifications non lues
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            int count = nonLues.size();
            
            // Publication du compteur sur plusieurs destinations
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, count);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications/count",
                count
            );
            
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @Operation(summary = "Obtenir les notifications non lues d'un candidat", 
               description = "Récupère toutes les notifications non lues pour un candidat spécifique")
    @ApiResponse(responseCode = "200", description = "Notifications récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Candidat non trouvé")
    @GetMapping("/unread/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsNonLues(
        @Parameter(description = "ID du candidat", required = true)
        @PathVariable Long candidatId) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getNotificationsNonLues(candidatId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @Operation(summary = "Obtenir toutes les notifications d'un candidat", 
               description = "Récupère toutes les notifications pour un candidat spécifique")
    @ApiResponse(responseCode = "200", description = "Notifications récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Candidat non trouvé")
    @GetMapping("/all/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getAllNotifications(
        @Parameter(description = "ID du candidat", required = true)
        @PathVariable Long candidatId) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getAllNotifications(candidatId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @Operation(summary = "Marquer toutes les notifications d'un candidat comme lues", 
               description = "Change le statut de toutes les notifications d'un candidat à 'lues'")
    @ApiResponse(responseCode = "200", description = "Notifications marquées comme lues")
    @ApiResponse(responseCode = "404", description = "Candidat non trouvé")
    @PutMapping("/read-all/{candidatId}")
    public ResponseEntity<String> marquerToutesCommeLues(
        @Parameter(description = "ID du candidat", required = true)
        @PathVariable Long candidatId) {
        try {
            notificationCandidatService.marquerToutesCommeLues(candidatId);
            
            // Notifier via WebSocket que toutes les notifications ont été lues
            // Envoyer la liste des notifications mise à jour
            List<NotificationCandidat> notifications = notificationCandidatService.getAllNotifications(candidatId);
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notifications);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications",
                notifications
            );
            
            // Mettre à jour le compteur (qui sera maintenant 0)
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, 0);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications/count",
                0
            );
            
            return ResponseEntity.ok("Toutes les notifications ont été marquées comme lues");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Erreur: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur serveur: " + e.getMessage());
        }
    }

    @Operation(summary = "Accepter un contact et créer une notification", 
               description = "Accepte une demande de contact et crée une notification associée")
    @ApiResponse(responseCode = "200", description = "Contact accepté et notification créée")
    @ApiResponse(responseCode = "404", description = "Contact non trouvé")
    @ApiResponse(responseCode = "400", description = "État invalide du contact")
    @PutMapping("/accept-contact/{contactId}")
    @Transactional
    public ResponseEntity<NotificationCandidat> acceptContactAndCreateNotification(
        @Parameter(description = "ID du contact", required = true)
        @PathVariable Long contactId) {
        try {
            NotificationCandidat notification = notificationCandidatService.acceptContactAndCreateNotification(contactId);
            
            // Notifier via WebSocket de la nouvelle notification
            Long candidatId = notification.getCandidat().getId();
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications",
                notification
            );
            
            // Envoyer le nombre mis à jour de notifications non lues
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            int count = nonLues.size();
            
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, count);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications/count",
                count
            );
            
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
 
    @Operation(summary = "Obtenir les notifications par candidat", 
               description = "Récupère toutes les notifications pour un candidat spécifique")
    @ApiResponse(responseCode = "200", description = "Notifications récupérées avec succès")
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<NotificationCandidat>> getNotificationsByCandidat(
        @Parameter(description = "ID du candidat", required = true)
        @PathVariable Long candidatId) {
        List<NotificationCandidat> notifications = notificationCandidatService.getByCandidatId(candidatId);
        return ResponseEntity.ok(notifications);
    }
    
    @Operation(summary = "Générer des notifications de matching pour une offre", 
               description = "Crée des notifications pour les candidats qui correspondent à une offre d'emploi")
    @ApiResponse(responseCode = "200", description = "Notifications générées avec succès")
    @PostMapping("/generer-offres-matching")
    public ResponseEntity<List<NotificationCandidat>> genererNotificationsMatching(
        @Parameter(description = "ID de l'offre d'emploi", required = true)
        @RequestParam Long offreId,
        @Parameter(description = "Seuil de matching (pourcentage)", example = "100")
        @RequestParam(defaultValue = "100") int seuilMatching) {

        List<NotificationCandidat> notifications =
            notificationCandidatService.genererNotificationsMatchingPourOffre(offreId, seuilMatching);
        
        // Pour chaque notification, informer le candidat concerné
        for(NotificationCandidat notification : notifications) {
            Long candidatId = notification.getCandidat().getId();
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notification);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications",
                notification
            );
            
            // Mettre à jour le compteur
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            int count = nonLues.size();
            
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, count);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications/count",
                count
            );
        }

        return ResponseEntity.ok(notifications);
    }
    
    // Endpoints WebSocket
    
    /**
     * Endpoint WebSocket pour s'abonner aux notifications d'un candidat
     */
    @MessageMapping("/subscribe/{candidatId}")
    public void subscribeToNotifications(@DestinationVariable Long candidatId) {
        try {
            List<NotificationCandidat> notifications = notificationCandidatService.getAllNotifications(candidatId);
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            messagingTemplate.convertAndSend("/topic/notifications/" + candidatId, notifications);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications",
                notifications
            );
            
            System.out.println("Abonnement aux notifications pour le candidat " + candidatId + 
                              " - " + notifications.size() + " notifications envoyées");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'abonnement aux notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Endpoint WebSocket pour s'abonner au compteur de notifications non lues
     */
    @MessageMapping("/subscribe/count/{candidatId}")
    public void subscribeToNotificationCount(@DestinationVariable Long candidatId) {
        try {
            List<NotificationCandidat> nonLues = notificationCandidatService.getNotificationsNonLues(candidatId);
            int count = nonLues.size();
            
            // Publication sur plusieurs destinations pour assurer la compatibilité
            messagingTemplate.convertAndSend("/topic/notifications/count/" + candidatId, count);
            messagingTemplate.convertAndSendToUser(
                String.valueOf(candidatId),
                "/queue/notifications/count",
                count
            );
            
            System.out.println("Abonnement au compteur de notifications pour le candidat " + candidatId + 
                              " - " + count + " notifications non lues");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'abonnement au compteur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
