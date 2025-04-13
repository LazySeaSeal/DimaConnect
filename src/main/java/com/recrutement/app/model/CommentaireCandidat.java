package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "commentaire_candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;
    
    @ManyToOne
    @JoinColumn(name = "publication_entreprise_id", nullable = false)
    private PublicationEntreprise publicationEntreprise;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Column(name = "est_lu")
    private Boolean estLu = false;
}
