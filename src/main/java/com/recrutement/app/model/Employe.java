package com.recrutement.app.model;
import com.recrutement.app.model.enums.RoleEmploye;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEmploye role;


    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(name = "date_inscription")
    private LocalDate dateInscription = LocalDate.now();
    
    private String telephone;


    // Relations
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
    @OneToMany(mappedBy = "evaluateur")
    private Set<Entretien> entretiensEvalues = new HashSet<>();
    
    @OneToMany(mappedBy = "createur")
    private Set<Test> testsCrees = new HashSet<>();
}
