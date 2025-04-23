package com.recrutement.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_entreprise")
public class ConversationEntreprise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long entreprise1Id;
    private Long entreprise2Id;
    
    private LocalDateTime dateCreation;
    private LocalDateTime derniereMiseAJour;
    private String statut; // ACTIVE, ARCHIVEE
    
    // Constructeurs
    public ConversationEntreprise() {}
    
    public ConversationEntreprise(Long entreprise1Id, Long entreprise2Id) {
        this.entreprise1Id = entreprise1Id;
        this.entreprise2Id = entreprise2Id;
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
}