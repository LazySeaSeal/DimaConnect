package com.recrutement.app.controller;

import com.recrutement.app.dto.EntrepriseDto;
import com.recrutement.app.service.EntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.recrutement.app.dto.CandidatProfileDTO;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
@RequiredArgsConstructor
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @GetMapping("/search")
    public List<EntrepriseDto> searchEntreprises(@RequestParam String keyword) {
        return entrepriseService.searchEntreprises(keyword);
    }

    @GetMapping("/{id}")
    public EntrepriseDto getEntreprise(@PathVariable Long id) {
        return entrepriseService.getEntrepriseDetails(id);
    }

    @GetMapping("/{entrepriseId}/contacts")
    public List<CandidatProfileDTO> getContactsInEntreprise(
            @PathVariable Long entrepriseId,
            @RequestParam Long candidatId) {

        return entrepriseService.getContactsInEntreprise(entrepriseId, candidatId);
    }
}
