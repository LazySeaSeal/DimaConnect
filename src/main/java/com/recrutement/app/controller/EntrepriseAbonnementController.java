package com.recrutement.app.controller;

import com.recrutement.app.dto.CandidatAbonneDTO;
import com.recrutement.app.service.AbonnesEntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseAbonnementController {

    private final AbonnesEntrepriseService abonnesEntrepriseService;

    @Autowired
    public EntrepriseAbonnementController(AbonnesEntrepriseService abonnesEntrepriseService) {
        this.abonnesEntrepriseService = abonnesEntrepriseService;
    }

    @GetMapping("/{entrepriseId}/abonnes")
    public ResponseEntity<List<CandidatAbonneDTO>> getAbonnesEntreprise(@PathVariable Long entrepriseId) {
        try {
            List<CandidatAbonneDTO> abonnes = abonnesEntrepriseService.getAbonnesEntreprise(entrepriseId);
            return ResponseEntity.ok(abonnes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{entrepriseId}/abonnes/verification")
    public ResponseEntity<Boolean> verifierAbonnement(
            @PathVariable Long entrepriseId,
            @RequestParam Long candidatId) {
        try {
            boolean estAbonne = abonnesEntrepriseService.isAbonne(entrepriseId, candidatId);
            return ResponseEntity.ok(estAbonne);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}