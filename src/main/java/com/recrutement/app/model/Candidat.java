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
@Table(name = "candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String prenom;
    
    @Column(name = "date_inscription")
    private LocalDate dateInscription = LocalDate.now();
    
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,3}[-\\s.]?[0-9]{3,4}[-\\s.]?[0-9]{3,4}$", message = "Format de téléphone invalide")
    private String telephone;
    
    @Size(max = 500, message = "La bio courte ne peut pas dépasser 500 caractères")
    @Column(name = "short_bio", length = 500)
    private String shortBio;
    
    @Size(max = 5000, message = "Le parcours ne peut pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String parcours;
    
    @Size(max = 5000, message = "La formation ne peut pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String formation;
    
    @Size(max = 5000, message = "L'expérience professionnelle ne peut pas dépasser 5000 caractères")
    @Column(name = "experience_professionnelle", columnDefinition = "TEXT")
    private String experienceProfessionnelle;
    
    @Size(max = 2000, message = "Les licences et certifications ne peuvent pas dépasser 2000 caractères")
    @Column(name = "licences_et_certifications", columnDefinition = "TEXT")
    private String licencesEtCertifications;
    
    private String cv;
    
    // Relations
    @ManyToMany
    @JoinTable(
        name = "competence_candidat",
        joinColumns = @JoinColumn(name = "candidat_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Set<Competence> competences = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<CompetenceCandidat> competenceCandidatDetails = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<PublicationCandidat> publications = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<NotificationCandidat> notifications = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<ContactCandidat> contacts = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<Abonnement> abonnements = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<Candidature> candidatures = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<CommentaireCandidat> commentaires = new HashSet<>();
    
    @OneToMany(mappedBy = "candidat")
    private Set<MessageEntreprise> messagesRecus = new HashSet<>();
}
