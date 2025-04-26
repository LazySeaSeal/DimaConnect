package com.recrutement.app.controller;

import com.recrutement.app.dto.OffreEmploiDTO;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.RoleEmploye;
import com.recrutement.app.service.OffreEmploiService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offres")
public class OffreEmploiController {

    @Autowired
    private OffreEmploiService offreEmploiService;

    /**
     * Créer une nouvelle offre d'emploi
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<OffreEmploi> creerOffre(@Valid @RequestBody OffreEmploi offreEmploi) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            OffreEmploi nouvelleOffre = offreEmploiService.creerOffre(offreEmploi, employeId);
            return new ResponseEntity<>(nouvelleOffre, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Obtenir toutes les offres en attente de validation
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'ADMIN')")
    public ResponseEntity<List<OffreEmploi>> getOffresEnAttente() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            // Récupérer l'ID de l'entreprise depuis l'employé connecté
            Long entrepriseId = offreEmploiService.getEntrepriseIdFromEmploye(employeId);

            List<OffreEmploi> offres = offreEmploiService.getOffresEnAttente(entrepriseId);
            return ResponseEntity.ok(offres);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Valider une offre d'emploi
     */
    @PutMapping("/{offreId}/valider")
   @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'ADMIN')")
    public ResponseEntity<OffreEmploi> validerOffre(@PathVariable Long offreId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            OffreEmploi offreValidee = offreEmploiService.validerOffre(offreId, employeId);
            return ResponseEntity.ok(offreValidee);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Rejeter une offre d'emploi
     */
    @PutMapping("/{offreId}/rejeter")
   @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'ADMIN')")
    public ResponseEntity<OffreEmploi> rejeterOffre(
            @PathVariable Long offreId,
            @RequestBody Map<String, String> motif) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            String motifRefus = motif.get("motif");
            if (motifRefus == null || motifRefus.trim().isEmpty()) {
                throw new IllegalArgumentException("Le motif de refus est obligatoire");
            }

            OffreEmploi offreRejetee = offreEmploiService.rejeterOffre(offreId, employeId, motifRefus);
            return ResponseEntity.ok(offreRejetee);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Modifier une offre d'emploi
     */
    @PutMapping("/{offreId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<OffreEmploi> modifierOffre(
            @PathVariable Long offreId,
            @Valid @RequestBody OffreEmploi offreDetails) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            OffreEmploi offreModifiee = offreEmploiService.modifierOffre(offreId, offreDetails, employeId);
            return ResponseEntity.ok(offreModifiee);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Supprimer une offre d'emploi
     */
    @DeleteMapping("/{offreId}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_RH', 'CHEF_PROJET', 'ADMIN')")
    public ResponseEntity<Void> supprimerOffre(@PathVariable Long offreId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long employeId = Long.parseLong(auth.getName());

            offreEmploiService.supprimerOffre(offreId, employeId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Rechercher des offres avec filtres
     */

    @GetMapping("/recherche")
    public ResponseEntity<Page<OffreEmploiDTO>> rechercherOffres(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePublication,
            @RequestParam(required = false) Integer niveauExpertise,
            @RequestParam(required = false) String typeContrat,
            @RequestParam(defaultValue = "pertinence") String tri,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille) {

        try {
            Page<OffreEmploi> offres = offreEmploiService.rechercherOffres(
                    titre, datePublication, niveauExpertise, typeContrat, tri, page, taille);

            // Convert to DTO
            Page<OffreEmploiDTO> offresDTOs = offres.map(OffreEmploiDTO::fromEntity);

            return ResponseEntity.ok(offresDTOs);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Obtenir une offre par son ID
     */
    @GetMapping("/{offreId}")
    public ResponseEntity<OffreEmploiDTO> getOffreById(@PathVariable Long offreId) {
        try {
            OffreEmploi offre = offreEmploiService.getOffreById(offreId)
                    .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

            return ResponseEntity.ok(OffreEmploiDTO.fromEntity(offre));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }




    /**
     * Rechercher des offres par compétences
     */
    @GetMapping("/par-competences")
    public ResponseEntity<Page<OffreEmploiDTO>> rechercherParCompetences(
            @RequestParam List<Long> competences,
            @RequestParam(defaultValue = "pertinence") String tri,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille) {

        try {
            Page<OffreEmploiDTO> offres = offreEmploiService.rechercherParCompetences(
                    competences, tri, page, taille);
            return ResponseEntity.ok(offres);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}