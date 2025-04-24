package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatureDTO;
import com.recrutement.app.model.*;
import com.recrutement.app.model.enums.StatutCandidature;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationEntrepriseService {

    private final CandidatureRepository candidatureRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final NotificationEntrepriseRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationEntrepriseService(
            CandidatureRepository candidatureRepository,
            CandidatRepository candidatRepository,
            OffreEmploiRepository offreEmploiRepository,
            NotificationEntrepriseRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.candidatureRepository = candidatureRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Long creerCandidature(CandidatureDTO candidatureDTO) {
        try {
            // Validation des entités existantes
            Candidat candidat = candidatRepository.findById(candidatureDTO.getCandidatId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Candidat non trouvé"));

            OffreEmploi offre = offreEmploiRepository.findById(candidatureDTO.getOffreEmploiId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Offre non trouvée"));

            // Création de la candidature
            Candidature candidature = new Candidature();
            candidature.setCandidat(candidat);
            candidature.setOffreEmploi(offre);
            candidature.setLettreMotivation(candidatureDTO.getLettreMotivation());
            candidature.setNoteEvaluation(candidatureDTO.getNoteEvaluation());
            candidature.setStatut(candidatureDTO.getStatut());
            candidature.setDatePostulation(LocalDate.now());

            Candidature savedCandidature = candidatureRepository.save(candidature);

            // Envoi de la notification
            envoyerNotificationEntreprise(savedCandidature);

            return savedCandidature.getId();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erreur lors de la création de la candidature: " + e.getMessage(), e);
        }
    }

    private void envoyerNotificationEntreprise(Candidature candidature) {
        try {
            NotificationEntreprise notification = new NotificationEntreprise();
            notification.setEntreprise(candidature.getOffreEmploi().getEntreprise());
            notification.setType(TypeNotification.CANDIDATURE);
            notification.setContenu(String.format(
                "Nouvelle candidature de %s %s pour l'offre: %s",
                candidature.getCandidat().getPrenom(),
                candidature.getCandidat().getNom(),
                candidature.getOffreEmploi().getTitre()
            ));
            notification.setItemReference(candidature.getId().toString());
            
            NotificationEntreprise savedNotification = notificationRepository.save(notification);
            
            // Envoi via WebSocket
            String destination = "/topic/entreprise-notifications/" 
                + candidature.getOffreEmploi().getEntreprise().getId();
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("candidatureId", candidature.getId());
            payload.put("offreId", candidature.getOffreEmploi().getId());
            payload.put("type", "NOUVELLE_CANDIDATURE");
            
            messagingTemplate.convertAndSend(destination, payload);
            
            // Confirmation au candidat
            String candidatDestination = "/topic/candidatures/" + candidature.getCandidat().getId();
            
            Map<String, Object> confirmationPayload = new HashMap<>();
            confirmationPayload.put("type", "CONFIRMATION");
            confirmationPayload.put("message", "Votre candidature a bien été envoyée");
            confirmationPayload.put("offreId", candidature.getOffreEmploi().getId());
            confirmationPayload.put("date", LocalDateTime.now().toString());
            
            messagingTemplate.convertAndSend(candidatDestination, confirmationPayload);
            
        } catch (Exception e) {
            // Log l'erreur mais ne pas interrompre le flux
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
}