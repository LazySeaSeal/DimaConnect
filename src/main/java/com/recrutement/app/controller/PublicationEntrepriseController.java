// PublicationEntrepriseController.java
package com.recrutement.app.controller;

import com.recrutement.app.dto.PublicationRequest;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.service.PublicationEntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publications/entreprises")
public class PublicationEntrepriseController {

    @Autowired
    private PublicationEntrepriseService publicationService;
    
    @PostMapping
    public ResponseEntity<PublicationEntreprise> createPublication(@RequestBody PublicationRequest request) {
        PublicationEntreprise createdPublication = publicationService.createPublication(request);
        return ResponseEntity.ok(createdPublication);
    }
}