package com.recrutement.app.controller;

import com.recrutement.app.dto.EntrepriseProfilDTO;
import com.recrutement.app.dto.EntrepriseUpdateDTO;
import com.recrutement.app.service.AdminEntrepriseProfilService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/entreprises")
public class AdminEntrepriseProfilController {

    private static final Logger logger = LoggerFactory.getLogger(AdminEntrepriseProfilController.class);

    @Autowired
    private AdminEntrepriseProfilService adminEntrepriseProfilService;

    /**
     * Met à jour le profil d'une entreprise
     */
    @PutMapping("/{id}/profil")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEntrepriseProfil(
            @PathVariable Long id,
            @Valid @RequestBody EntrepriseUpdateDTO updateDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Update request received from: {}", auth.getName());

        // Vérifier que l'admin a les droits sur cette entreprise
        if (!adminEntrepriseProfilService.isAdminOfEntreprise(id)) {
            logger.warn("Access denied for user: {} on enterprise: {}", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vous n'avez pas les droits d'administration pour cette entreprise");
        }

        Optional<EntrepriseProfilDTO> profil = adminEntrepriseProfilService.updateEntrepriseProfil(id, updateDTO);

        if (profil.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profil.get());
    }

    /**
     * Vérifie une entreprise (certification)
     */
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifierEntreprise(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Verify request received from: {}", auth.getName());

        // Vérifier que l'admin a les droits sur cette entreprise
        if (!adminEntrepriseProfilService.isAdminOfEntreprise(id)) {
            logger.warn("Access denied for user: {} on enterprise: {}", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vous n'avez pas les droits d'administration pour cette entreprise");
        }

        Optional<EntrepriseProfilDTO> profil = adminEntrepriseProfilService.verifierEntreprise(id);

        if (profil.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profil.get());
    }

    /**
     * Active/désactive le statut premium d'une entreprise
     */
    @PatchMapping("/{id}/premium")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> togglePremiumStatus(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate dateExpiration) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Premium toggle request received from: {}", auth.getName());
        logger.debug("User authorities: {}", auth.getAuthorities());

        // Dump authentication details to understand what's happening
        if (auth.getPrincipal() instanceof UserDetails) {
            logger.debug("Principal is UserDetails: {}", ((UserDetails)auth.getPrincipal()).getUsername());
        } else {
            logger.debug("Principal is: {} (class: {})", auth.getPrincipal(),
                    auth.getPrincipal() != null ? auth.getPrincipal().getClass().getName() : "null");
        }

        // Vérifier que l'admin a les droits sur cette entreprise
        if (!adminEntrepriseProfilService.isAdminOfEntreprise(id)) {
            logger.warn("Access denied for user: {} on enterprise: {}", auth.getName(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vous n'avez pas les droits d'administration pour cette entreprise");
        }

        Optional<EntrepriseProfilDTO> profil = adminEntrepriseProfilService.togglePremiumStatus(id, dateExpiration);

        if (profil.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profil.get());
    }
}