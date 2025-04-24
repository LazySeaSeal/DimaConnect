package com.recrutement.app.controller;

import com.recrutement.app.dto.CandidatureDTO;
import com.recrutement.app.service.NotificationEntrepriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidatures")
@Tag(name = "Candidatures", description = "Gestion des candidatures")
public class CandidatureController {

    private final NotificationEntrepriseService candidatureService;

    public CandidatureController(NotificationEntrepriseService candidatureService) {
        this.candidatureService = candidatureService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle candidature")
    @ApiResponse(responseCode = "201", description = "Candidature créée avec succès")
    @ApiResponse(responseCode = "404", description = "Candidat ou offre non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    public ResponseEntity<Long> creerCandidature(
            @RequestBody CandidatureDTO candidatureDTO) {
        
        try {
            Long candidatureId = candidatureService.creerCandidature(candidatureDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(candidatureId);
            
        } catch (Exception e) {
            throw e; // La gestion d'erreur est déjà faite dans le service
        }
    }
}