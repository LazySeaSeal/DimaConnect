package com.recrutement.app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.recrutement.app.model.enums.StatutOffre;
import com.recrutement.app.model.enums.TypeContrat;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offre_emploi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreEmploi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "createur_id", nullable = false)
    private Employe createur;

    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private Employe validateur;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100, message = "Le titre doit contenir entre 5 et 100 caractères")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 50, max = 5000, message = "La description doit contenir entre 50 et 5000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le type de contrat est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "type_contrat", nullable = false)
    private TypeContrat typeContrat;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(min = 2, max = 100, message = "La localisation doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String localisation;

    @Pattern(regexp = "^[0-9]{1,6}(\\.[0-9]{1,2})?(€|\\$|£)?$|^à négocier$", message = "Format de salaire invalide")
    private String salaire;

    @NotNull(message = "La date d'expiration est obligatoire")
    @Future(message = "La date d'expiration doit être dans le futur")
    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    @Column(name = "date_validation")
    private LocalDate dateValidation;

    @Column(name = "est_active")
    private Boolean estActive = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutOffre statut = StatutOffre.EN_ATTENTE;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @Column(name = "niveau_expertise")
    private Integer niveauExpertise;

    // Relations
    // Option 1: Supprimer la relation many-to-many pour éviter les doubles insertions
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //         name = "competence_offre",
    //         joinColumns = @JoinColumn(name = "offre_emploi_id"),
    //         inverseJoinColumns = @JoinColumn(name = "competence_id")
    // )
    // private Set<Competence> competences = new HashSet<>();

    // Option 2: Conserver la relation pour compatibilité mais utiliser
    // mappedBy pour indiquer que CompetenceOffre est propriétaire
    // Cette approche est préférable si vous avez besoin de rétrocompatibilité
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "competence_offre_legacy",  // Table différente pour éviter les conflits
            joinColumns = @JoinColumn(name = "offre_emploi_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Set<Competence> competences = new HashSet<>();

    @OneToMany(mappedBy = "offreEmploi")
    private Set<Candidature> candidatures = new HashSet<>();

    @OneToMany(mappedBy = "offre")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "offreEmploi", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<CompetenceOffre> competenceOffreDetails = new HashSet<>();

    // Initialize collections after bean construction
    @PostConstruct
    public void initialize() {
        if (this.competences == null) {
            this.competences = new HashSet<>();
        }
        if (this.competenceOffreDetails == null) {
            this.competenceOffreDetails = new HashSet<>();
        }
    }

    // Synchroniser les compétences avec competenceOffreDetails
    // Méthode utilitaire pour maintenir la cohérence entre les deux collections
    public void synchroniserCompetences() {
        this.competences.clear();
        if (this.competenceOffreDetails != null) {
            for (CompetenceOffre co : this.competenceOffreDetails) {
                if (co.getCompetence() != null) {
                    this.competences.add(co.getCompetence());
                }
            }
        }
    }

    // Explicit Getters and Setters for competences and competenceOffreDetails
    public Set<Competence> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<Competence> competences) {
        this.competences = competences;
    }

    public Set<CompetenceOffre> getCompetenceOffreDetails() {
        return competenceOffreDetails;
    }

    public void setCompetenceOffreDetails(Set<CompetenceOffre> competenceOffreDetails) {
        this.competenceOffreDetails = competenceOffreDetails;
    }
}