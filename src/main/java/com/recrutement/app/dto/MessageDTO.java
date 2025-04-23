package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Objet de transfert de données pour les messages")
public class MessageDTO {
    
    @Schema(description = "ID unique du message", example = "1")
    private Long id;
    
    @Schema(description = "Contenu textuel du message", example = "Bonjour, je suis intéressé par votre offre")
    private String contenu;
    
    @Schema(description = "Date d'envoi du message", example = "2023-05-15")
    private LocalDate dateEnvoi;
    
    @Schema(description = "Statut de lecture du message", example = "false")
    private boolean estLu;
    
    @Schema(description = "Type de l'expéditeur", 
            allowableValues = {"CANDIDAT", "ENTREPRISE"}, 
            example = "CANDIDAT")
    private String type;
    
    @Schema(description = "ID de l'expéditeur", example = "1")
    private Long expediteurId;
    
    @Schema(description = "ID du destinataire", example = "2")
    private Long destinataireId;
    
    @Schema(description = "ID de la conversation associée", example = "1")
    private Long conversationId;

    public MessageDTO() {
    }

    public MessageDTO(Long id, String contenu, LocalDate dateEnvoi, boolean estLu, 
                     String type, Long expediteurId, Long destinataireId, Long conversationId) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.estLu = estLu;
        this.type = type;
        this.expediteurId = expediteurId;
        this.destinataireId = destinataireId;
        this.conversationId = conversationId;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDate getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDate dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpediteurId() {
        return expediteurId;
    }

    public void setExpediteurId(Long expediteurId) {
        this.expediteurId = expediteurId;
    }

    public Long getDestinataireId() {
        return destinataireId;
    }

    public void setDestinataireId(Long destinataireId) {
        this.destinataireId = destinataireId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", dateEnvoi=" + dateEnvoi +
                ", estLu=" + estLu +
                ", type='" + type + '\'' +
                ", expediteurId=" + expediteurId +
                ", destinataireId=" + destinataireId +
                ", conversationId=" + conversationId +
                '}';
    }
}