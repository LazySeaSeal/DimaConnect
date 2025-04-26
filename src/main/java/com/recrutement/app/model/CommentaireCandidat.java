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
    @JoinColumn(name = "publication_entreprise_id")
    private PublicationEntreprise publicationEntreprise;
    
    @ManyToOne
    @JoinColumn(name = "publication_candidat_id")
    private PublicationCandidat publicationCandidat;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Column(name = "est_lu")
    private Boolean estLu = false;

    // Business logic to ensure one and only one publication is set
    @PrePersist
    @PreUpdate
    private void validate() {
        if (publicationCandidat == null && publicationEntreprise == null) {
            throw new IllegalStateException("Either publicationCandidat or publicationEntreprise must be set");
        }
        if (publicationCandidat != null && publicationEntreprise != null) {
            throw new IllegalStateException("Only one of publicationCandidat or publicationEntreprise can be set");
        }
    }
}