package com.recrutement.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "entreprise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String nom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Pattern(regexp = "^[0-9]{14}$", message = "Le SIRET doit contenir 14 chiffres")
    @Column(length = 14, unique = true)
    private String siret;
    
    @URL(message = "L'URL du site doit être valide")
    @Column(name = "url_site")
    private String urlSite;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Min(value = 1, message = "Le nombre d'employés doit être au moins 1")
    @Column(name = "nombre_employes")
    private Integer nombreEmployes;
    
    @Size(max = 100, message = "Le secteur d'activité ne peut pas dépasser 100 caractères")
    @Column(name = "secteur_activite")
    private String secteurActivite;
    
    @Column(name = "est_premium")
    private Boolean estPremium = false;
    
    @Future(message = "La date d'expiration doit être dans le futur")
    @Column(name = "date_expiration")
    private LocalDate dateExpiration;
    
    @Column(name = "est_verifiee")
    private Boolean estVerifiee = false;
    
    // Relations
    @OneToMany(mappedBy = "entreprise")
    private Set<Employe> employes = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<Statistique> statistiques = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<PublicationEntreprise> publications = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<NotificationEntreprise> notifications = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<ContactCandidat> contacts = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<Abonnement> abonnements = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<AbonnementPremium> abonnementsPremium = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<OffreEmploi> offresEmploi = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<CommentaireEntreprise> commentaires = new HashSet<>();
    
    @OneToMany(mappedBy = "entreprise")
    private Set<MessageEntreprise> messages = new HashSet<>();

    @OneToMany(mappedBy = "entreprise")
    private Set<Evenement> evenements = new HashSet<>();


}
