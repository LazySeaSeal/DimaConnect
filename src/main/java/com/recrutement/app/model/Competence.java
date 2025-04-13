package com.recrutement.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "competence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    @Column(nullable = false)
    private String categorie;
}
