package com.recrutement.app.controller;

import com.recrutement.app.dto.EntrepriseInscriptionDto;
import com.recrutement.app.exception.ResourceAlreadyExistsException;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.service.EntrepriseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @Autowired
    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireEntreprise(@Valid @RequestBody EntrepriseInscriptionDto inscriptionDto) {
        try {
            // Vérifier que les mots de passe correspondent
            if (!inscriptionDto.getMotDePasse().equals(inscriptionDto.getConfirmerMotDePasse())) {
                return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas");
            }

            Entreprise entrepriseCreee = entrepriseService.inscrireEntreprise(inscriptionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entrepriseCreee);
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Afficher la trace complète dans les logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de l'inscription: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Entreprise>> obtenirToutesEntreprises() {
        List<Entreprise> entreprises = entrepriseService.obtenirToutesEntreprises();
        return ResponseEntity.ok(entreprises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirEntrepriseParId(@PathVariable Long id) {
        return entrepriseService.obtenirEntrepriseParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> mettreAJourEntreprise(@PathVariable Long id, @Valid @RequestBody Entreprise entrepriseDetails) {
        try {
            Entreprise entrepriseMiseAJour = entrepriseService.mettreAJourEntreprise(id, entrepriseDetails);
            return ResponseEntity.ok(entrepriseMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEntreprise(@PathVariable Long id) {
        entrepriseService.supprimerEntreprise(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/verifier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifierEntreprise(@PathVariable Long id) {
        try {
            entrepriseService.verifierEntreprise(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Entreprise vérifiée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}