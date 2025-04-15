package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class MessageCandidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long candidatId;
    private Long entrepriseId;
    private String contenu;
    private LocalDate dateEnvoi;
    private Boolean estLu;
    
    // Nouveau champ pour la relation avec Conversation
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    // Constructeurs
    public MessageCandidat() {
    }
    
    public MessageCandidat(Long candidatId, Long entrepriseId, String contenu, LocalDate dateEnvoi, Boolean estLu) {
        this.candidatId = candidatId;
        this.entrepriseId = entrepriseId;
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
    
    public Long getCandidatId() {
        return candidatId;
    }
    
    public void setCandidatId(Long candidatId) {
        this.candidatId = candidatId;
    }
    
    public Long getEntrepriseId() {
        return entrepriseId;
    }
    
    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
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
    
    // Nouveau getter et setter pour conversation
    public Conversation getConversation() {
        return conversation;
    }
    
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}