package com.recrutement.app.service;

import com.recrutement.app.dto.PublicationRequest;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.model.Candidat;  // Assurez-vous que Candidat est bien importé
import com.recrutement.app.repository.PublicationCandidatRepository;
import com.recrutement.app.repository.CandidatRepository; // Ajout du repository Candidat
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationCandidatService {

    @Autowired
    private PublicationCandidatRepository publicationRepository;

    @Autowired
    private CandidatRepository candidatRepository; // Ajout du repository pour accéder aux candidats

    public PublicationCandidat createPublication(PublicationRequest request) {
        PublicationCandidat publication = new PublicationCandidat();

        // Définir les propriétés de la publication à partir de la requête
        publication.setContenu(request.getContenu());
        publication.setMediaUrl(request.getMediaUrl());
        publication.setTypeMedia(request.getTypeMedia());
        publication.setDatePublication(java.time.LocalDate.now());
        publication.setNombreLikes(0);

        // Récupérer le candidat à partir de l'ID
        Candidat candidat = candidatRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec ID : " + request.getOwnerId()));

        // Associer le candidat à la publication
        publication.setCandidat(candidat); // Lier le candidat à la publication

        // Sauvegarder la publication
        return publicationRepository.save(publication);
    }

    public List<PublicationCandidat> getPublicationsByCandidatId(Long candidatId) {
        return publicationRepository.findByCandidatId(candidatId);
    }

    public PublicationCandidat getPublicationById(Long id) {
        Optional<PublicationCandidat> publication = publicationRepository.findById(id);
        return publication.orElse(null);
    }

    public PublicationCandidat updatePublication(Long id, PublicationRequest request) {
        Optional<PublicationCandidat> existingPublication = publicationRepository.findById(id);
        if (existingPublication.isPresent()) {
            PublicationCandidat publication = existingPublication.get();
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

    public PublicationCandidat addLike(Long id) {
        Optional<PublicationCandidat> publication = publicationRepository.findById(id);
        if (publication.isPresent()) {
            PublicationCandidat pub = publication.get();
            pub.setNombreLikes(pub.getNombreLikes() + 1);
            return publicationRepository.save(pub);
        }
        return null;
    }

    public PublicationCandidat removeLike(Long id) {
        Optional<PublicationCandidat> publication = publicationRepository.findById(id);
        if (publication.isPresent()) {
            PublicationCandidat pub = publication.get();
            if (pub.getNombreLikes() > 0) {
                pub.setNombreLikes(pub.getNombreLikes() - 1);
            }
            return publicationRepository.save(pub);
        }
        return null;
    }
}
