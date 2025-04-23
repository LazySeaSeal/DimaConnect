package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation_candidat")
public class ConversationCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long candidat1Id;
    
    @Column(nullable = false)
    private Long candidat2Id;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    private LocalDateTime derniereMiseAJour;
    private String statut = "ACTIVE"; // par défaut: "ACTIVE", peut être "ARCHIVEE"
    
    // Relation avec les messages
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<MessageCandidatToCandidat> messages = new ArrayList<>();
    
    // Constructeurs
    public ConversationCandidat() {
    }
    
    public ConversationCandidat(Long candidat1Id, Long candidat2Id) {
        this.candidat1Id = candidat1Id;
        this.candidat2Id = candidat2Id;
        this.dateCreation = LocalDateTime.now();
        this.derniereMiseAJour = LocalDateTime.now();
    }
    
    // Getters et setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCandidat1Id() {
        return candidat1Id;
    }
    
    public void setCandidat1Id(Long candidat1Id) {
        this.candidat1Id = candidat1Id;
    }
    
    public Long getCandidat2Id() {
        return candidat2Id;
    }
    
    public void setCandidat2Id(Long candidat2Id) {
        this.candidat2Id = candidat2Id;
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
    
    public List<MessageCandidatToCandidat> getMessages() {
        return messages;
    }
    
    public void setMessages(List<MessageCandidatToCandidat> messages) {
        this.messages = messages;
    }
    
    // Méthodes utiles
    public void addMessage(MessageCandidatToCandidat message) {
        messages.add(message);
        message.setConversation(this);
        this.derniereMiseAJour = LocalDateTime.now();
    }
}