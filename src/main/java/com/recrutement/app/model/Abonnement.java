package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "abonnement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(name = "date_abonnement")
    private LocalDate dateAbonnement = LocalDate.now();
}