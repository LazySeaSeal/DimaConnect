package com.recrutement.app.model;

import com.recrutement.app.model.enums.StatutContact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "contact_candidat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactCandidat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_candidat_id", nullable = false)
    private Candidat sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_candidat_id", nullable = false)
    private Candidat receiver;
    
    // Add this to satisfy the existing mapping in Candidat
    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;
    
    @Column(name = "date_connexion")
    private LocalDate dateConnexion = LocalDate.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutContact statut;
}