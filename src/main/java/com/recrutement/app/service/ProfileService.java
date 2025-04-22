package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.mapper.CandidatMapper;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final CandidatRepository candidatRepository;
    private final CandidatMapper candidatMapper;

    @Autowired
    public ProfileService(CandidatRepository candidatRepository, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.candidatMapper = candidatMapper;
    }

    public CandidatProfileDTO getCandidatProfile(Long id) {
        Candidat candidat = candidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return candidatMapper.toProfileDTO(candidat);
    }

    @Transactional
    public CandidatProfileDTO updateProfile(Long id, CandidatProfileDTO profileDTO) {
        Candidat candidat = candidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        candidat.setShortBio(profileDTO.getShortBio());
        candidat.setExperienceProfessionnelle(profileDTO.getExperienceProfessionnelle());
        candidat.setFormation(profileDTO.getFormation());
        candidat.setLicencesEtCertifications(profileDTO.getLicencesEtCertifications());
        candidat.setTelephone(profileDTO.getTelephone());

        if (profileDTO.getCv() != null) {
            candidat.setCv(profileDTO.getCv());
        }

        Candidat updatedCandidat = candidatRepository.save(candidat);
        return candidatMapper.toProfileDTO(updatedCandidat);
    }
}
