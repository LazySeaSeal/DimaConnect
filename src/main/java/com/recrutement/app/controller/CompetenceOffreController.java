package com.recrutement.app.controller;

import com.recrutement.app.model.Competence;
import com.recrutement.app.model.CompetenceOffre;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.service.CompetenceOffreService;
import com.recrutement.app.service.OffreEmploiService;
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
@RequestMapping("/api/offres/{offreId}/competences")
public class CompetenceOffreController {

    @Autowired
    private OffreEmploiService offreEmploiService;

    @Autowired
    private CompetenceOffreService competenceOffreService;

    /**
     * Ajouter des compétences à une offre d'emploi
     */
    @PostMapping
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
    @GetMapping
    public ResponseEntity<Set<CompetenceOffre>> getCompetencesOffre(@PathVariable Long offreId) {
        try {
            Set<CompetenceOffre> competences = competenceOffreService.getCompetencesParOffre(offreId);
            return ResponseEntity.ok(competences);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Mettre à jour une compétence spécifique de l'offre
     */
    @PutMapping("/{competenceOffreId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<CompetenceOffre> mettreAJourCompetence(
            @PathVariable Long offreId,
            @PathVariable Long competenceOffreId,
            @Valid @RequestBody CompetenceOffre competenceOffre) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            CompetenceOffre updated = competenceOffreService.mettreAJourCompetence(
                    offreId, competenceOffreId, competenceOffre, employeId);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Supprimer une compétence de l'offre
     */
    @DeleteMapping("/{competenceOffreId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<Void> supprimerCompetence(
            @PathVariable Long offreId,
            @PathVariable Long competenceOffreId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            competenceOffreService.supprimerCompetence(offreId, competenceOffreId, employeId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}