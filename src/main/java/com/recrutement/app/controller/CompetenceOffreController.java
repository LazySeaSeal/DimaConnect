package com.recrutement.app.controller;

import com.recrutement.app.model.CompetenceOffre;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.service.CompetenceOffreService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/offres")
public class CompetenceOffreController {

    @Autowired
    private CompetenceOffreService competenceOffreService;

    /**
     * Ajouter une compétence à une offre
     */
    @PostMapping("/{offreId}/competences/{competenceId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<CompetenceOffre> ajouterCompetence(
            @PathVariable Long offreId,
            @PathVariable Long competenceId,
            @Valid @RequestBody(required = false) CompetenceOffre details) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            // Si aucun détail n'est fourni, créez un objet vide
            if (details == null) {
                details = new CompetenceOffre();
            }

            CompetenceOffre competenceOffre = competenceOffreService.ajouterCompetence(
                    offreId, competenceId, details, employeId);

            return new ResponseEntity<>(competenceOffre, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Ajouter plusieurs compétences à une offre d'emploi
     */
    @PostMapping("/{offreId}/competences")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<OffreEmploi> ajouterCompetences(
            @PathVariable Long offreId,
            @Valid @RequestBody List<CompetenceOffre> competences) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            OffreEmploi offre = competenceOffreService.ajouterCompetences(offreId, competences, employeId);
            return ResponseEntity.ok(offre);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Obtenir toutes les compétences d'une offre
     */
    @GetMapping("/{offreId}/competences")
    public ResponseEntity<Set<CompetenceOffre>> getCompetencesParOffre(@PathVariable Long offreId) {
        try {
            Set<CompetenceOffre> competences = competenceOffreService.getCompetencesParOffre(offreId);
            return ResponseEntity.ok(competences);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Mettre à jour une compétence de l'offre
     */
    @PutMapping("/{offreId}/competences/{competenceId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<CompetenceOffre> mettreAJourCompetence(
            @PathVariable Long offreId,
            @PathVariable Long competenceId,
            @Valid @RequestBody CompetenceOffre competenceOffre) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            CompetenceOffre updatedCompetence = competenceOffreService.mettreAJourCompetence(
                    offreId, competenceId, competenceOffre, employeId);
            return ResponseEntity.ok(updatedCompetence);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Supprimer une compétence de l'offre
     */
    @DeleteMapping("/{offreId}/competences/{competenceId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<Void> supprimerCompetence(
            @PathVariable Long offreId,
            @PathVariable Long competenceId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            competenceOffreService.supprimerCompetence(offreId, competenceId, employeId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}