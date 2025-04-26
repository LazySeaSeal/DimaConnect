package com.recrutement.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.recrutement.app.model.enums.RoleEmploye;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitation_employe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationEmploye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateExpiration;

    @Column(nullable = false)
    private boolean estUtilisee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEmploye role;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Méthode pour générer un token unique
    public static InvitationEmploye creerInvitation(String email, RoleEmploye role, Entreprise entreprise) {
        InvitationEmploye invitation = new InvitationEmploye();
        invitation.setEmail(email);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setDateCreation(LocalDateTime.now());
        invitation.setDateExpiration(LocalDateTime.now().plusDays(7)); // Expiration après 7 jours
        invitation.setEstUtilisee(false);
        invitation.setRole(role);
        invitation.setEntreprise(entreprise);
        return invitation;
    }

    public boolean estValide() {
        return !estUtilisee && LocalDateTime.now().isBefore(dateExpiration);
    }
}