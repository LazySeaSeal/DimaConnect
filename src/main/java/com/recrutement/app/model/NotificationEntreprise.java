package com.recrutement.app.model;

import com.recrutement.app.model.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "notification_entreprise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntreprise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();
    
    @Column(name = "est_lue")
    private Boolean estLue = false;
    
    @Column(name = "item_reference")
    private String itemReference;
}
