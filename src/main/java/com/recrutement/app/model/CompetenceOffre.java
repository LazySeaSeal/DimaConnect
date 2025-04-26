package com.recrutement.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.recrutement.app.model.enums.NiveauImportance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "competence_offre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceOffre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauImportance importance; // C'est ce champ qui pose problème

    @Column(nullable = false)
    private Integer niveau; // Ce champ est aussi obligatoire

    @Column(name = "est_obligatoire")
    private Boolean estObligatoire = false; // Avec une valeur par défaut

    @ManyToOne
    @JoinColumn(name = "offre_emploi_id", nullable = false)
    @JsonBackReference
    private OffreEmploi offreEmploi;
}
