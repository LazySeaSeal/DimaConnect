package com.recrutement.app.controller;

import com.recrutement.app.model.NotificationEntreprise;
import com.recrutement.app.service.NotificationEntrepriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/entreprise")
@Tag(name = "Notifications Entreprise", description = "Gestion des notifications pour les entreprises")
public class NotificationEntrepriseController {

    private final NotificationEntrepriseService notificationService;

    @Autowired
    public NotificationEntrepriseController(NotificationEntrepriseService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{entrepriseId}/non-lues")
    @Operation(summary = "Récupérer notifications non lues", description = "Récupère toutes les notifications non lues pour une entreprise donnée")
    @ApiResponse(responseCode = "200", description = "Liste des notifications non lues")
    public ResponseEntity<List<NotificationEntreprise>> getNotificationsNonLues(
            @Parameter(description = "ID de l'entreprise") @PathVariable Long entrepriseId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(entrepriseId));
    }

    @PostMapping("/abonnement")
    @Operation(summary = "Créer un abonnement", description = "Crée un abonnement entre un candidat et une entreprise et envoie une notification")
    @ApiResponse(responseCode = "201", description = "Abonnement créé avec succès")
    @ApiResponse(responseCode = "400", description = "Abonnement existe déjà")
    public ResponseEntity<String> creerAbonnement(
            @RequestParam Long entrepriseId,
            @RequestParam Long candidatId) {
        return notificationService.creerAbonnement(entrepriseId, candidatId);
    }

    @PatchMapping("/{notificationId}/marquer-lue")
    @Operation(summary = "Marquer notification comme lue", description = "Marque une notification spécifique comme lue")
    @ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    public ResponseEntity<Void> marquerNotificationCommeLue(
            @PathVariable Long notificationId) {
        notificationService.marquerNotificationCommeLue(notificationId);
        return ResponseEntity.ok().build();
    }
}