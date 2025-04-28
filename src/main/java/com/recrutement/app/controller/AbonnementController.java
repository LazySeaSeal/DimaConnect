package com.recrutement.app.controller;
import com.recrutement.app.dto.EntrepriseDto;

import com.recrutement.app.dto.AbonnementRequest;
import com.recrutement.app.service.AbonnementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementService abonnementService;

    @PostMapping("/follow")
    public void subscribeToEntreprise(@RequestBody AbonnementRequest request) {
        abonnementService.subscribeToEntreprise(request);
    }

    @GetMapping("/mes-abonnements/{candidatId}")
    public List<EntrepriseDto> getMyAbonnements(@PathVariable Long candidatId) {
        return abonnementService.getAbonnementsByCandidat(candidatId);
    }
}
