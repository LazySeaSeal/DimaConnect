package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "commentaire_entreprise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireEntreprise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
    @ManyToOne
    @JoinColumn(name = "publication_candidat_id", nullable = true)
    private PublicationCandidat publicationCandidat;
    
    @ManyToOne
    @JoinColumn(name = "publication_entreprise_id", nullable = true)
    private PublicationEntreprise publicationEntreprise;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Column(name = "est_lu")
    private Boolean estLu = false;

    // Add validation to ensure one of publication fields is set
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