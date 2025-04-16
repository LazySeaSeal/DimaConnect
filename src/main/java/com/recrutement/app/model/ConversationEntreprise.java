package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ConversationEntreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long entreprise1Id;
    
    @Column(nullable = false)
    private Long entreprise2Id;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    private LocalDateTime derniereMiseAJour;
    private String statut = "ACTIVE"; // par défaut: "ACTIVE", peut être "ARCHIVEE"
    
    // Relation avec les messages
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<MessageEntrepriseToEntreprise> messages = new ArrayList<>();
    
    // Constructeurs
    public ConversationEntreprise() {
    }
    
    public ConversationEntreprise(Long entreprise1Id, Long entreprise2Id) {
        this.entreprise1Id = entreprise1Id;
        this.entreprise2Id = entreprise2Id;
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
    
    public Long getEntreprise1Id() {
        return entreprise1Id;
    }
    
    public void setEntreprise1Id(Long entreprise1Id) {
        this.entreprise1Id = entreprise1Id;
    }
    
    public Long getEntreprise2Id() {
        return entreprise2Id;
    }
    
    public void setEntreprise2Id(Long entreprise2Id) {
        this.entreprise2Id = entreprise2Id;
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
    
    public List<MessageEntrepriseToEntreprise> getMessages() {
        return messages;
    }
    
    public void setMessages(List<MessageEntrepriseToEntreprise> messages) {
        this.messages = messages;
    }
    
    // Méthodes utiles
    public void addMessage(MessageEntrepriseToEntreprise message) {
        messages.add(message);
        message.setConversation(this);
        this.derniereMiseAJour = LocalDateTime.now();
    }
}