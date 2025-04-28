package com.recrutement.app.controller;

import com.recrutement.app.dto.CandidatureRequestDTO;
import com.recrutement.app.service.CandidatureCandidatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidatures")
@Tag(name = "Candidatures - Candidat", description = "APIs for candidates to apply to offers")
public class CandidatureCandidatController {

    private final CandidatureCandidatService candidatureCandidatService;

    @Autowired
    public CandidatureCandidatController(CandidatureCandidatService candidatureCandidatService) {
        this.candidatureCandidatService = candidatureCandidatService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply to an offer")
    public ResponseEntity<Void> applyToOffer(@Valid @RequestBody CandidatureRequestDTO candidatureRequestDTO) {
        candidatureCandidatService.applyToOffer(candidatureRequestDTO);
        return ResponseEntity.ok().build();
    }
}
