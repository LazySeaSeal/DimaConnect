package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeQuestion;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "L'offre est obligatoire")
    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreEmploi offre;
    
    @NotBlank(message = "Le texte de la question est obligatoire")
    @Size(min = 5, max = 1000, message = "Le texte doit contenir entre 5 et 1000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;
    
    @NotNull(message = "Le type de question est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeQuestion type;
    
    @Column(columnDefinition = "TEXT")
    private String reponse;
    
    @Column(name = "reponse_correcte", columnDefinition = "TEXT")
    private String reponseCorrecte;
    
    @Min(value = 0, message = "Les points ne peuvent pas être négatifs")
    @Max(value = 100, message = "Les points ne peuvent pas dépasser 100")
    private Integer points = 0;
    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false) // Ensure this column is present in the database
    private Test test;

}
