package com.recrutement.app.controller;

import com.recrutement.app.dto.EntrepriseProfilDTO;
import com.recrutement.app.service.EntrepriseProfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseProfilController {

    @Autowired
    private EntrepriseProfilService entrepriseProfilService;

    @GetMapping("/{id}/profil")
    @PreAuthorize("hasAnyRole('CHEF_PROJET', 'RESPONSABLE_RH', 'ADMIN')")
    public ResponseEntity<EntrepriseProfilDTO> getProfilEntreprise(@PathVariable Long id) {
        Optional<EntrepriseProfilDTO> profil = entrepriseProfilService.getProfilEntreprise(id);

        if (profil.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profil.get());
    }
}