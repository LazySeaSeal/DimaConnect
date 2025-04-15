// PublicationEntrepriseService.java
package com.recrutement.app.service;

import com.recrutement.app.dto.PublicationRequest;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PublicationEntrepriseService {

    @Autowired
    private PublicationEntrepriseRepository publicationRepository;
    
    @Autowired
    private EntrepriseRepository entrepriseRepository;
    
    public PublicationEntreprise createPublication(PublicationRequest request) {
        Entreprise entreprise = entrepriseRepository.findById(request.getOwnerId()).orElse(null);
        
        PublicationEntreprise publication = new PublicationEntreprise();
        publication.setEntreprise(entreprise);
        publication.setContenu(request.getContenu());
        publication.setMediaUrl(request.getMediaUrl());
        publication.setTypeMedia(request.getTypeMedia());
        publication.setDatePublication(LocalDate.now());
        publication.setNombreLikes(0);
        
        return publicationRepository.save(publication);
    }
}