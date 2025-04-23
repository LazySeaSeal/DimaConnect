package com.recrutement.app.controller;

import com.recrutement.app.dto.PaiementDTO;
import com.recrutement.app.exception.PaiementNotFoundException;
import com.recrutement.app.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@Validated // This ensures validation is performed on the DTOs
public class PaiementController {

    private final PaiementService paiementService;

    @Autowired
    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    // Create a new Paiement
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Sends a 201 status on successful creation
    public PaiementDTO createPaiement(@RequestBody @Valid PaiementDTO paiementDTO) {
        return paiementService.savePaiement(paiementDTO);
    }

    // Get all Paiements
    @GetMapping
    public List<PaiementDTO> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    // Get Paiement by ID
    @GetMapping("/{id}")
    public PaiementDTO getPaiementById(@PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found with ID: " + id));
    }

    // Delete Paiement by ID
    @DeleteMapping("/{id}")
    public void deletePaiement(@PathVariable Long id) {
        if (!paiementService.existsById(id)) {
            throw new PaiementNotFoundException("Paiement not found with ID: " + id);
        }
        paiementService.deletePaiement(id);
    }
}
