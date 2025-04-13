package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeMedia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publication_candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "media_url")
    private String mediaUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_media")
    private TypeMedia typeMedia;
    
    @Column(name = "date_publication")
    private LocalDate datePublication = LocalDate.now();
    
    @Column(name = "nombre_likes")
    private Integer nombreLikes = 0;
    
    @OneToMany(mappedBy = "publicationCandidat")
    private Set<CommentaireEntreprise> commentaires = new HashSet<>();
}
