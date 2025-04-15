package com.recrutement.app.service;

import com.recrutement.app.dto.PublicationRequest;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.model.Entreprise;  // Assurez-vous que Entreprise est bien importé
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import com.recrutement.app.repository.EntrepriseRepository; // Ajout du repository Entreprise
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationEntrepriseService {

    @Autowired
    private PublicationEntrepriseRepository publicationRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository; // Ajout du repository pour accéder aux entreprises

    public PublicationEntreprise createPublication(PublicationRequest request) {
        PublicationEntreprise publication = new PublicationEntreprise();

        // Définir les propriétés de la publication à partir de la requête
        publication.setContenu(request.getContenu());
        publication.setMediaUrl(request.getMediaUrl());
        publication.setTypeMedia(request.getTypeMedia());
        publication.setDatePublication(java.time.LocalDate.now());
        publication.setNombreLikes(0);

        // Récupérer le entreprise à partir de l'ID
        Entreprise entreprise = entrepriseRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvé avec ID : " + request.getOwnerId()));

        // Associer le entreprise à la publication
        publication.setEntreprise(entreprise); // Lier le entreprise à la publication

        // Sauvegarder la publication
        return publicationRepository.save(publication);
    }

    public List<PublicationEntreprise> getPublicationsByEntrepriseId(Long entrepriseId) {
        return publicationRepository.findByEntrepriseId(entrepriseId);
    }

    public PublicationEntreprise getPublicationById(Long id) {
        Optional<PublicationEntreprise> publication = publicationRepository.findById(id);
        return publication.orElse(null);
    }

    public PublicationEntreprise updatePublication(Long id, PublicationRequest request) {
        Optional<PublicationEntreprise> existingPublication = publicationRepository.findById(id);
        if (existingPublication.isPresent()) {
            PublicationEntreprise publication = existingPublication.get();
            publication.setContenu(request.getContenu());
            publication.setMediaUrl(request.getMediaUrl());
            publication.setTypeMedia(request.getTypeMedia());
            return publicationRepository.save(publication);
        }
        return null;
    }

    public boolean deletePublication(Long id) {
        if (publicationRepository.existsById(id)) {
            publicationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public PublicationEntreprise addLike(Long id) {
        Optional<PublicationEntreprise> publication = publicationRepository.findById(id);
        if (publication.isPresent()) {
            PublicationEntreprise pub = publication.get();
            pub.setNombreLikes(pub.getNombreLikes() + 1);
            return publicationRepository.save(pub);
        }
        return null;
    }

    public PublicationEntreprise removeLike(Long id) {
        Optional<PublicationEntreprise> publication = publicationRepository.findById(id);
        if (publication.isPresent()) {
            PublicationEntreprise pub = publication.get();
            if (pub.getNombreLikes() > 0) {
                pub.setNombreLikes(pub.getNombreLikes() - 1);
            }
            return publicationRepository.save(pub);
        }
        return null;
    }
}
