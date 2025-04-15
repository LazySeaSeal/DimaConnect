package com.recrutement.app.dto;

import java.time.LocalDate;

public class MessageDTO {
    private Long id;
    private String contenu;
    private LocalDate dateEnvoi;
    private Boolean estLu;
    private String expediteur; // "CANDIDAT" or "ENTREPRISE"
    private Long expediteurId;
    private Long destinataireId;
    private Long conversationId;

    // Constructors
    public MessageDTO() {}

    public MessageDTO(Long id, String contenu, LocalDate dateEnvoi, Boolean estLu, 
                     String expediteur, Long expediteurId, Long destinataireId, Long conversationId) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.estLu = estLu;
        this.expediteur = expediteur;
        this.expediteurId = expediteurId;
        this.destinataireId = destinataireId;
        this.conversationId = conversationId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDate getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDate dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public Boolean getEstLu() { return estLu; }
    public void setEstLu(Boolean estLu) { this.estLu = estLu; }
    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }
    public Long getExpediteurId() { return expediteurId; }
    public void setExpediteurId(Long expediteurId) { this.expediteurId = expediteurId; }
    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long destinataireId) { this.destinataireId = destinataireId; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}