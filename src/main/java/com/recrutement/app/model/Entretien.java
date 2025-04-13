package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeEntretien;
import com.recrutement.app.model.enums.ResultatEntretien;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Entity
@Table(name = "entretien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entretien {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La candidature est obligatoire")
    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;
    
    @NotNull(message = "Le type d'entretien est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEntretien type;
    
    @NotNull(message = "La date est obligatoire")
    @Future(message = "La date doit être dans le futur")
    @Column(nullable = false)
    private LocalDate date;
    
    @Min(value = 15, message = "La durée minimale est de 15 minutes")
    @Max(value = 240, message = "La durée maximale est de 240 minutes")
    private Integer duree;  // en minutes
    
    @URL(message = "Le lien doit être une URL valide")
    private String lien;
    
    @Column(name = "est_visio_conference")
    private Boolean estVisioConference = false;
    
    @ManyToOne
    @JoinColumn(name = "evaluateur_id")
    private Employe evaluateur;
    
    @ManyToOne
    @JoinColumn(name = "postulant_id")
    private Candidat postulant;
    
    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    
    @Enumerated(EnumType.STRING)
    private ResultatEntretien resultat;
}
