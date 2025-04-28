package com.recrutement.app.service;

import com.recrutement.app.dto.OffreEmploiDto;
import com.recrutement.app.mapper.OffreEmploiMapper;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OffreCandidatService {

    private final OffreEmploiRepository offreEmploiRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiMapper offreEmploiMapper;

    @Autowired
    public OffreCandidatService(OffreEmploiRepository offreEmploiRepository,
                                CandidatRepository candidatRepository,
                                OffreEmploiMapper offreEmploiMapper) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiMapper = offreEmploiMapper;
    }

    public List<OffreEmploiDto> searchOffersByKeyword(String keyword) {
        return offreEmploiRepository
                .findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(offreEmploiMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OffreEmploiDto> searchOffersByLocation(String location) {
        return offreEmploiRepository
                .findByLocalisationContainingIgnoreCase(location)
                .stream()
                .map(offreEmploiMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OffreEmploiDto> findOffersMatchingCandidateProfile(Long candidatId) {
        var candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));

        var candidateSkills = candidat.getCompetences();

        return offreEmploiRepository.findAll().stream()
                .filter(offer -> !offer.getCompetences().isEmpty() &&
                        offer.getCompetences().stream().anyMatch(candidateSkills::contains))
                .map(offreEmploiMapper::toDto)
                .collect(Collectors.toList());
    }
}
