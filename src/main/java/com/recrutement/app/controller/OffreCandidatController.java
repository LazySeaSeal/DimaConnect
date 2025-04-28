package com.recrutement.app.controller;

import com.recrutement.app.dto.OffreEmploiDto;
import com.recrutement.app.service.OffreCandidatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@Tag(name = "Offres - Candidat", description = "APIs for candidates to search job offers")
public class OffreCandidatController {

    private final OffreCandidatService offreCandidatService;

    @Autowired
    public OffreCandidatController(OffreCandidatService offreCandidatService) {
        this.offreCandidatService = offreCandidatService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search offers by keyword (title/description)")
    public ResponseEntity<List<OffreEmploiDto>> searchOffers(@RequestParam String keyword) {
        return ResponseEntity.ok(offreCandidatService.searchOffersByKeyword(keyword));
    }

    @GetMapping("/location")
    @Operation(summary = "Search offers by location")
    public ResponseEntity<List<OffreEmploiDto>> searchOffersByLocation(@RequestParam String location) {
        return ResponseEntity.ok(offreCandidatService.searchOffersByLocation(location));
    }

    @GetMapping("/profile-matches/{candidatId}")
    @Operation(summary = "Find offers matching candidate profile")
    public ResponseEntity<List<OffreEmploiDto>> findOffersMatchingProfile(@PathVariable Long candidatId) {
        return ResponseEntity.ok(offreCandidatService.findOffersMatchingCandidateProfile(candidatId));
    }
}
