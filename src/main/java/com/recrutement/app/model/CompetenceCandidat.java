package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "competence_candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;
    
    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;
    
    @Column(nullable = false)
    private Integer niveau;
}
