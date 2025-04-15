package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long candidatId;
    
    @Column(nullable = false)
    private Long entrepriseId;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    private LocalDateTime derniereMiseAJour;
    
    private String statut; // par exemple: "ACTIVE", "ARCHIVEE", "TERMINEE"
    
    // Relations avec les messages
    @OneToMany(mappedBy = "conversation")
    private List<MessageCandidat> messagesCandidat = new ArrayList<>();
    
    @OneToMany(mappedBy = "conversation") 
    private List<MessageEntreprise> messagesEntreprise = new ArrayList<>();
    
    // Constructeurs
    public Conversation() {
    }
    
    public Conversation(Long candidatId, Long entrepriseId) {
        this.candidatId = candidatId;
        this.entrepriseId = entrepriseId;
        this.dateCreation = LocalDateTime.now();
        this.derniereMiseAJour = LocalDateTime.now();
        this.statut = "ACTIVE";
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
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDerniereMiseAJour() {
        return derniereMiseAJour;
    }
    
    public void setDerniereMiseAJour(LocalDateTime derniereMiseAJour) {
        this.derniereMiseAJour = derniereMiseAJour;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public List<MessageCandidat> getMessagesCandidat() {
        return messagesCandidat;
    }
    
    public void setMessagesCandidat(List<MessageCandidat> messagesCandidat) {
        this.messagesCandidat = messagesCandidat;
    }
    
    public List<MessageEntreprise> getMessagesEntreprise() {
        return messagesEntreprise;
    }
    
    public void setMessagesEntreprise(List<MessageEntreprise> messagesEntreprise) {
        this.messagesEntreprise = messagesEntreprise;
    }
    
    // Méthodes utiles
    public void addMessageCandidat(MessageCandidat message) {
        messagesCandidat.add(message);
        message.setConversation(this);
        this.derniereMiseAJour = LocalDateTime.now();
    }
    
    public void addMessageEntreprise(MessageEntreprise message) {
        messagesEntreprise.add(message);
        message.setConversation(this);
        this.derniereMiseAJour = LocalDateTime.now();
    }
}