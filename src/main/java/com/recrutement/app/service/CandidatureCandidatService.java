package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatureRequestDTO;
import com.recrutement.app.model.Candidature;
import com.recrutement.app.model.enums.StatutCandidature;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.CandidatureRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidatureCandidatService {

    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CandidatureRepository candidatureRepository;

    @Autowired
    public CandidatureCandidatService(CandidatRepository candidatRepository,
                                      OffreEmploiRepository offreEmploiRepository,
                                      CandidatureRepository candidatureRepository) {
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.candidatureRepository = candidatureRepository;
    }

    @Transactional
    public void applyToOffer(CandidatureRequestDTO dto) {
        var candidat = candidatRepository.findById(dto.getCandidatId())
                .orElseThrow(() -> new RuntimeException("Candidat not found"));
        var offre = offreEmploiRepository.findById(dto.getOffreId())
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        Candidature candidature = new Candidature();
        candidature.setCandidat(candidat);
        candidature.setOffreEmploi(offre);
        candidature.setLettreMotivation(dto.getLettreMotivation());
        candidature.setStatut(StatutCandidature.SOUMISE);

        candidatureRepository.save(candidature);
    }
}
