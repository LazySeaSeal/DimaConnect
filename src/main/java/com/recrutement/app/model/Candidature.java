package com.recrutement.app.model;

import com.recrutement.app.model.enums.StatutCandidature;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "candidature")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Le candidat est obligatoire")
    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;
    
    @NotNull(message = "L'offre d'emploi est obligatoire")
    @ManyToOne
    @JoinColumn(name = "offre_emploi_id", nullable = false)
    private OffreEmploi offreEmploi;
    
    @Column(name = "date_postulation")
    private LocalDate datePostulation = LocalDate.now();
    
    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut = StatutCandidature.SOUMISE;
    
    @Size(max = 5000, message = "La lettre de motivation ne peut pas dépasser 5000 caractères")
    @Column(name = "lettre_motivation", columnDefinition = "TEXT")
    private String lettreMotivation;
    
    @Size(max = 500, message = "La note du recruteur ne peut pas dépasser 500 caractères")
    @Column(name = "note_recruteur", length = 500)
    private String noteRecruteur;
    
    @DecimalMin(value = "0.0", message = "La note d'évaluation doit être positive")
    @DecimalMax(value = "20.0", message = "La note d'évaluation ne peut pas dépasser 20")
    @Column(name = "note_evaluation")
    private Float noteEvaluation;
    
    @OneToMany(mappedBy = "candidature")
    private Set<Entretien> entretiens = new HashSet<>();
}
