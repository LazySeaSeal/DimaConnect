package com.recrutement.app.controller;

import com.recrutement.app.dto.EntretienRequestDTO;
import com.recrutement.app.dto.EntretienResponseDTO;
import com.recrutement.app.service.EntretienService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api/entretiens")
@RequiredArgsConstructor
public class EntretienController {

    private final EntretienService entretienService;

    // Planifier un entretien
    @PostMapping
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    public ResponseEntity<EntretienResponseDTO> planifierEntretien(@RequestBody EntretienRequestDTO entretienDTO) {
        EntretienResponseDTO createdEntretien = entretienService.planifierEntretien(entretienDTO);
        return ResponseEntity.ok(createdEntretien);
    }

    // Modifier un entretien
    // @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH') or hasRole('ADMIN')")
    @PutMapping("/{entretienId}")
    public ResponseEntity<EntretienResponseDTO> modifierEntretien(
            @PathVariable Long entretienId,
            @RequestBody EntretienRequestDTO entretienDTO
    ) {
        EntretienResponseDTO updatedEntretien = entretienService.modifierEntretien(entretienId, entretienDTO);
        return ResponseEntity.ok(updatedEntretien);
    }

    // Supprimer un entretien
    // @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH') or hasRole('ADMIN')")
    @DeleteMapping("/{entretienId}")
    public ResponseEntity<Void> supprimerEntretien(@PathVariable Long entretienId) {
        entretienService.supprimerEntretien(entretienId);
        return ResponseEntity.ok().build();
    }

    // les entretiens il kol mte3 candidat (by id)
    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<EntretienResponseDTO>> getEntretiensByCandidat(@PathVariable Long candidatId) {
        List<EntretienResponseDTO> entretiens = entretienService.getEntretiensByCandidatId(candidatId);
        return ResponseEntity.ok(entretiens);
    }

    // Planification automatique
    //@PreAuthorize("hasRole('RECRUTEUR') or hasRole('RH')")
    @PostMapping("/planification-automatique")
    public ResponseEntity<EntretienResponseDTO> planificationAutomatique(@RequestBody EntretienRequestDTO entretienDTO) {
        EntretienResponseDTO plannedEntretien = entretienService.planificationAutomatique(entretienDTO);
        return ResponseEntity.ok(plannedEntretien);
    }
}