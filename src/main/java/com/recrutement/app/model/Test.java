package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "test")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;
    
    @Column(name = "points_reussite", nullable = false)
    private Float pointsReussite;
    
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Employe createur;
    
    @OneToMany(mappedBy = "test")
    private Set<Question> questions = new HashSet<>();
}
