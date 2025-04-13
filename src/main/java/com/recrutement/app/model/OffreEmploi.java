package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeContrat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offre_emploi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreEmploi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
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
    
    @Column(name = "est_active")
    private Boolean estActive = true;
    
    @ManyToMany
    @JoinTable(
        name = "competence_offre",
        joinColumns = @JoinColumn(name = "offre_emploi_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Set<Competence> competences = new HashSet<>();
    
    @OneToMany(mappedBy = "offreEmploi")
    private Set<CompetenceOffre> competenceOffreDetails = new HashSet<>();
    
    @OneToMany(mappedBy = "offreEmploi")
    private Set<Candidature> candidatures = new HashSet<>();
    
    @OneToMany(mappedBy = "offre")
    private Set<Question> questions = new HashSet<>();
}
