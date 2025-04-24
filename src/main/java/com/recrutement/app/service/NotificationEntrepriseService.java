package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatureDTO;
import com.recrutement.app.model.*;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
@Tag(name = "Notification Entreprise Service", description = "Gestion des notifications pour les entreprises")
public class NotificationEntrepriseService {

    private final CandidatureRepository candidatureRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final NotificationEntrepriseRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AbonnementRepository abonnementRepository;
    private final EntrepriseRepository entrepriseRepository;

    public NotificationEntrepriseService(
            CandidatureRepository candidatureRepository,
            CandidatRepository candidatRepository,
            OffreEmploiRepository offreEmploiRepository,
            NotificationEntrepriseRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate,
            AbonnementRepository abonnementRepository,
            EntrepriseRepository entrepriseRepository) {
        this.candidatureRepository = candidatureRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.abonnementRepository = abonnementRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    @Transactional
    @Operation(summary = "Créer une candidature", description = "Crée une nouvelle candidature et envoie une notification à l'entreprise")
    @ApiResponse(responseCode = "200", description = "Candidature créée avec succès")
    @ApiResponse(responseCode = "404", description = "Candidat ou offre non trouvé")
    public Long creerCandidature(CandidatureDTO candidatureDTO) {
        try {
            Candidat candidat = candidatRepository.findById(candidatureDTO.getCandidatId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidat non trouvé"));

            OffreEmploi offre = offreEmploiRepository.findById(candidatureDTO.getOffreEmploiId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre non trouvée"));

            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setOffreEmploi(offre);
            candidature.setLettreMotivation(candidatureDTO.getLettreMotivation());
            candidature.setNoteEvaluation(candidatureDTO.getNoteEvaluation());
            candidature.setStatut(candidatureDTO.getStatut());
            candidature.setDatePostulation(LocalDate.now());

            Candidature savedCandidature = candidatureRepository.save(candidature);
            envoyerNotificationEntreprise(savedCandidature);
            return savedCandidature.getId();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de la candidature: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Operation(summary = "Créer un abonnement", description = "Crée un abonnement entre un candidat et une entreprise")
    @ApiResponse(responseCode = "201", description = "Abonnement créé avec succès")
    @ApiResponse(responseCode = "400", description = "Abonnement existe déjà")
    public ResponseEntity<String> creerAbonnement(Long entrepriseId, Long candidatId) {
        try {
            // Vérifier si l'abonnement existe déjà
            Optional<Abonnement> existantOpt = abonnementRepository.findByCandidatIdAndEntrepriseId(candidatId, entrepriseId);
            if (existantOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Un abonnement existe déjà entre ce candidat et cette entreprise");
            }

            // Vérifier l'existence de l'entreprise
            Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise non trouvée"));

            // Vérifier l'existence du candidat
            Candidat candidat = candidatRepository.findById(candidatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidat non trouvé"));

            // Créer le nouvel abonnement
            Abonnement nouvelAbonnement = new Abonnement();
            nouvelAbonnement.setEntreprise(entreprise);
            nouvelAbonnement.setCandidat(candidat);
            nouvelAbonnement.setDateAbonnement(LocalDate.now());

            // Sauvegarder l'abonnement
            Abonnement abonnementCree = abonnementRepository.save(nouvelAbonnement);
            
            // Envoyer la notification
            envoyerNotificationAbonnement(entrepriseId, candidatId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Abonnement créé avec succès. ID: " + abonnementCree.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la création de l'abonnement: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Envoyer notification abonnement", description = "Envoie une notification lorsqu'un candidat s'abonne à une entreprise")
    @ApiResponse(responseCode = "200", description = "Notification envoyée avec succès")
    public void envoyerNotificationAbonnement(Long entrepriseId, Long candidatId) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidat non trouvé"));

        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise non trouvée"));

        NotificationEntreprise notification = new NotificationEntreprise();
        notification.setEntreprise(entreprise);
        notification.setType(TypeNotification.ABONNEMENT);
        notification.setContenu(String.format("Le candidat %s %s s'est abonné à votre entreprise",
                candidat.getPrenom(), candidat.getNom()));
        notification.setDateCreation(LocalDate.now());
        notification.setEstLue(false);
        notification.setItemReference("abonnement-" + candidatId);

        NotificationEntreprise savedNotification = notificationRepository.save(notification);

        String destination = "/topic/entreprise-notifications/" + entreprise.getId();
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NOUVEL_ABONNEMENT");
        payload.put("candidatId", candidatId);
        payload.put("candidatNom", candidat.getPrenom() + " " + candidat.getNom());
        payload.put("notificationId", savedNotification.getId());

        messagingTemplate.convertAndSend(destination, payload);
    }

    @Transactional
    @Operation(summary = "Marquer notification comme lue", description = "Marque une notification comme lue")
    @ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    public void marquerNotificationCommeLue(Long notificationId) {
        NotificationEntreprise notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvée"));

        notification.setEstLue(true);
        notificationRepository.save(notification);
    }

    @Operation(summary = "Récupérer notifications non lues", description = "Récupère les notifications non lues pour une entreprise")
    @ApiResponse(responseCode = "200", description = "Liste des notifications non lues")
    public List<NotificationEntreprise> getNotificationsNonLues(Long entrepriseId) {
        return notificationRepository.findByEntrepriseIdAndEstLueFalse(entrepriseId);
    }

    private void envoyerNotificationEntreprise(Candidature candidature) {
        try {
            NotificationEntreprise notification = new NotificationEntreprise();
            notification.setEntreprise(candidature.getOffreEmploi().getEntreprise());
            notification.setType(TypeNotification.CANDIDATURE);
            notification.setContenu(String.format("Nouvelle candidature de %s %s pour l'offre: %s",
                    candidature.getCandidat().getPrenom(),
                    candidature.getCandidat().getNom(),
                    candidature.getOffreEmploi().getTitre()));
            notification.setItemReference(candidature.getId().toString());
            notification.setDateCreation(LocalDate.now());
            notification.setEstLue(false);

            NotificationEntreprise savedNotification = notificationRepository.save(notification);

            String destination = "/topic/entreprise-notifications/" +
                    candidature.getOffreEmploi().getEntreprise().getId();

            Map<String, Object> payload = new HashMap<>();
            payload.put("candidatureId", candidature.getId());
            payload.put("offreId", candidature.getOffreEmploi().getId());
            payload.put("type", "NOUVELLE_CANDIDATURE");
            payload.put("notificationId", savedNotification.getId());

            messagingTemplate.convertAndSend(destination, payload);

            // Confirmation au candidat
            String candidatDestination = "/topic/candidatures/" + candidature.getCandidat().getId();
            Map<String, Object> confirmationPayload = new HashMap<>();
            confirmationPayload.put("type", "CONFIRMATION");
            confirmationPayload.put("message", "Votre candidature a bien été envoyée");
            confirmationPayload.put("offreId", candidature.getOffreEmploi().getId());
            confirmationPayload.put("date", LocalDate.now().toString());

            messagingTemplate.convertAndSend(candidatDestination, confirmationPayload);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'envoi de la notification", e);
        }
    }
}