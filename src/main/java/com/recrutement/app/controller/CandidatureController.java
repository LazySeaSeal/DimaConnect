package com.recrutement.app.controller;

import com.recrutement.app.dto.CreateCandidatureDTO;
import com.recrutement.app.model.Candidature;
import com.recrutement.app.model.enums.StatutCandidature;
import com.recrutement.app.service.CandidatureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;




import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;

    public CandidatureController(CandidatureService candidatureService) {
        this.candidatureService = candidatureService;
    }
    // Consulter les candidatures d'une offre d'emploi
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<Candidature>> getCandidaturesByOffre(@PathVariable Long offreId) {
        List<Candidature> candidatures = candidatureService.getCandidaturesByOffre(offreId);
        return ResponseEntity.ok(candidatures);
    }
    //@PreAuthorize("hasRole('CANDIDAT')")
    @PostMapping
    public ResponseEntity<Candidature> ajouterCandidature(@Valid @RequestBody CreateCandidatureDTO dto) {
        Candidature candidature = candidatureService.ajouterCandidature(dto);
        return ResponseEntity.ok(candidature); // Retourne la candidature créée
    }


    // Valider une candidature
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @PutMapping("/{candidatureId}/valider")
    public ResponseEntity<Candidature> validerCandidature(@PathVariable Long candidatureId) {
        Candidature candidature = candidatureService.validerCandidature(candidatureId);
        return ResponseEntity.ok(candidature);
    }

    // Rejeter une candidature avec un commentaire
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @PutMapping("/{candidatureId}/rejeter")
    public ResponseEntity<Candidature> rejeterCandidature(
            @PathVariable Long candidatureId,
            @RequestBody(required = false) String noteRecruteur) {
        Candidature candidature = candidatureService.rejeterCandidature(candidatureId, noteRecruteur);
        return ResponseEntity.ok(candidature);
    }
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @GetMapping("/search")
    public ResponseEntity<List<Candidature>> searchCandidatures(
            @RequestParam(required = false) Long candidatId,
            @RequestParam(required = false) Long offreEmploiId,
            @RequestParam(required = false) String candidatName,
            @RequestParam(required = false) StatutCandidature statut
    ) {
        List<Candidature> results = candidatureService.searchCandidatures(candidatId, offreEmploiId, candidatName, statut);
        return ResponseEntity.ok(results);
    }

}