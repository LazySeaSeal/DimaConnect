package com.recrutement.app.service;

import com.recrutement.app.dto.CreateCandidatureDTO;
import com.recrutement.app.model.Candidature;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.StatutCandidature;
import com.recrutement.app.repository.CandidatureRepository;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              CandidatRepository candidatRepository,
                              OffreEmploiRepository offreEmploiRepository) {
        this.candidatureRepository = candidatureRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
    }
    @Transactional
    public Candidature ajouterCandidature(CreateCandidatureDTO createCandidatureDTO) {
        // Vérifier si le candidat existe
        Candidat candidat = candidatRepository.findById(createCandidatureDTO.getCandidatId())
                .orElseThrow(() -> new RuntimeException("Candidat introuvable"));

        // Vérifier si l'offre d'emploi existe
        OffreEmploi offreEmploi = offreEmploiRepository.findById(createCandidatureDTO.getOffreEmploiId())
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable"));

        // Créer une nouvelle candidature
        Candidature candidature = new Candidature();
        candidature.setCandidat(candidat);
        candidature.setOffreEmploi(offreEmploi);
        candidature.setLettreMotivation(createCandidatureDTO.getLettreMotivation());
        candidature.setStatut(StatutCandidature.SOUMISE); // Définir le statut initial comme "SOUMISE"

        // Enregistrer la candidature
        return candidatureRepository.save(candidature);
    }



    // Recuperer les candidatures liées a une offre demploi
    public List<Candidature> getCandidaturesByOffre(Long offreEmploiId) {
        return candidatureRepository.findByOffreEmploiId(offreEmploiId);
    }

    // Valider une candidature
    @Transactional
    public Candidature validerCandidature(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        return candidatureRepository.save(candidature);
    }

    // Rejeter une candidature
    @Transactional
    public Candidature rejeterCandidature(Long candidatureId, String noteRecruteur) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));
        candidature.setStatut(StatutCandidature.REFUSEE);
        candidature.setNoteRecruteur(noteRecruteur);
        return candidatureRepository.save(candidature);
    }
    //search
    public List<Candidature> searchCandidatures(Long candidatId, Long offreEmploiId, String candidatName, StatutCandidature statut) {
        // If all filters are provided
        if (candidatId != null && offreEmploiId != null && candidatName != null && statut != null) {
            return candidatureRepository.findByOffreEmploiIdAndStatut(offreEmploiId, statut).stream()
                    .filter(c -> c.getCandidat().getId().equals(candidatId)
                            && c.getCandidat().getNom().toLowerCase().contains(candidatName.toLowerCase()))
                    .toList();
        }

        // Filter by candidatId and status
        if (candidatId != null && statut != null) {
            return candidatureRepository.findByCandidatIdAndStatut(candidatId, statut);
        }

        // Filter by job offer ID and status
        if (offreEmploiId != null && statut != null) {
            return candidatureRepository.findByOffreEmploiIdAndStatut(offreEmploiId, statut);
        }

        // Filter by job offer ID and candidate name
        if (offreEmploiId != null && candidatName != null) {
            return candidatureRepository.findByOffreEmploiIdAndCandidatNomContainingIgnoreCase(offreEmploiId, candidatName);
        }

        // Filter only by status
        if (statut != null) {
            return candidatureRepository.findByStatut(statut);
        }

        // Filter only by candidate name
        if (candidatName != null) {
            return candidatureRepository.findByCandidatNomContainingIgnoreCase(candidatName);
        }

        // Filter by job offer ID only
        if (offreEmploiId != null) {
            return candidatureRepository.findByOffreEmploiId(offreEmploiId);
        }

        // Filter by candidate ID only
        if (candidatId != null) {
            return candidatureRepository.findByCandidatId(candidatId);
        }

        // If no filters applied, return all candidatures
        return candidatureRepository.findAll();
    }


}
