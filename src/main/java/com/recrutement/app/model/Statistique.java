package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeStatistique;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "statistique")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistique {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeStatistique type;
    
    private String periode;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Column(name = "est_lue")
    private Boolean estLue = false;
    
    // Relations
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
}
