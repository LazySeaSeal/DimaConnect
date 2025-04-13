package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeAbonnementRef;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "abonnement_premium")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbonnementPremium {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
    @ManyToOne
    @JoinColumn(name = "pack_id", nullable = false)
    private PackPremium pack;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    
    @Column(name = "montant_paye", nullable = false)
    private Float montantPaye;
    
    @Column(name = "est_actif")
    private Boolean estActif = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAbonnementRef ref;
}
