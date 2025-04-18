
package com.recrutement.app.controller;

import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.service.NotificationCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import com.recrutement.app.dto.MatchingRequest;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationCandidatController {
    @Autowired
    private NotificationCandidatService notificationCandidatService;

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

// Nouvelle API POST pour générer les notifications
@PostMapping("/generer-offres-matching")
public ResponseEntity<List<NotificationCandidat>> genererNotificationsMatching(
        @RequestBody MatchingRequest request) {
    
    List<NotificationCandidat> nouvellesNotifications = 
        notificationCandidatService.genererNotificationsOffreMatching(
            request.getCandidatId(),
            request.getSeuilMatching());
    
    return ResponseEntity.ok(nouvellesNotifications);
}
}