package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class MessageEntrepriseToEntreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long expediteurId;
    private Long destinataireId;
    private String contenu;
    private LocalDate dateEnvoi;
    private Boolean estLu;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private ConversationEntreprise conversation;
    
    // Constructeurs
    public MessageEntrepriseToEntreprise() {
    }
    
    public MessageEntrepriseToEntreprise(Long expediteurId, Long destinataireId, String contenu, LocalDate dateEnvoi, Boolean estLu) {
        this.expediteurId = expediteurId;
        this.destinataireId = destinataireId;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.estLu = estLu;
    }
    
    // Getters et setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Boolean getEstLu() {
        return estLu;
    }
    
    public void setEstLu(Boolean estLu) {
        this.estLu = estLu;
    }
    
    public ConversationEntreprise getConversation() {
        return conversation;
    }
    
    public void setConversation(ConversationEntreprise conversation) {
        this.conversation = conversation;
    }
}