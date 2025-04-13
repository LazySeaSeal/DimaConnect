package com.recrutement.app.model;

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
    @JoinColumn(name = "offre_emploi_id", nullable = false)
    private OffreEmploi offreEmploi;
    
    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauImportance importance;
    
    @Column(nullable = false)
    private Integer niveau;
    
    @Column(name = "est_obligatoire")
    private Boolean estObligatoire = false;
}
