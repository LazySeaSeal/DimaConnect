package com.recrutement.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pack_premium")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackPremium {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String nom;
    
    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Column(nullable = false)
    private Float prix;
    
    @NotNull(message = "La durée en jours est obligatoire")
    @Min(value = 1, message = "La durée doit être d'au moins 1 jour")
    @Column(name = "duree_jours", nullable = false)
    private Integer dureeJours;
    
    @Min(value = 1, message = "Le nombre d'offres maximum doit être d'au moins 1")
    @Column(name = "nombre_offres_max", nullable = false)
    private Integer nombreOffresMax;
    
    @Min(value = 0, message = "Le nombre de candidatures visibles ne peut pas être négatif")
    @Column(name = "nombre_candidatures_visibles")
    private Integer nombreCandidaturesVisibles;
    
    @Column(name = "est_premium")
    private Boolean estPremium = true;
    
    @OneToMany(mappedBy = "pack")
    private Set<AbonnementPremium> abonnements = new HashSet<>();
}
