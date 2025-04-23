package com.recrutement.app.dto;

import java.time.LocalDate;

public class PaiementDTO {

    private Long id;
    private Float montant;
    private LocalDate datePaiement;
    private Long entrepriseId;

    // Constructors
    public PaiementDTO() {}

    public PaiementDTO(Long id, Float montant, LocalDate datePaiement, Long entrepriseId) {
        this.id = id;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.entrepriseId = entrepriseId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Float getMontant() { return montant; }
    public void setMontant(Float montant) { this.montant = montant; }

    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    public Long getEntrepriseId() { return entrepriseId; }
    public void setEntrepriseId(Long entrepriseId) { this.entrepriseId = entrepriseId; }
}
