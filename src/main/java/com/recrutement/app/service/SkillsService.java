package com.recrutement.app.service;

import com.recrutement.app.dto.CompetenceRatingDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.Competence;
import com.recrutement.app.model.CompetenceCandidat;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.CompetenceCandidatRepository;
import com.recrutement.app.repository.CompetenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillsService {

    private final CompetenceRepository competenceRepository;
    private final CompetenceCandidatRepository competenceCandidatRepository;
    private final CandidatRepository candidatRepository;

    @Autowired
    public SkillsService(CompetenceRepository competenceRepository, CompetenceCandidatRepository competenceCandidatRepository, CandidatRepository candidatRepository) {
        this.competenceRepository = competenceRepository;
        this.competenceCandidatRepository = competenceCandidatRepository;
        this.candidatRepository = candidatRepository;
    }

    public List<CompetenceRatingDTO> getCandidateSkills(Long candidatId) {
        return competenceCandidatRepository.findByCandidatId(candidatId).stream()
                .map(cc -> {
                    CompetenceRatingDTO dto = new CompetenceRatingDTO();
                    dto.setCompetenceId(cc.getCompetence().getId());
                    dto.setNom(cc.getCompetence().getNom());
                    dto.setCategorie(cc.getCompetence().getCategorie());
                    dto.setNiveau(cc.getNiveau());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addOrUpdateSkill(Long candidatId, CompetenceRatingDTO skillDTO) {
        Competence competence = competenceRepository.findById(skillDTO.getCompetenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        CompetenceCandidat competenceCandidat = competenceCandidatRepository
                .findByCandidatIdAndCompetenceId(candidatId, skillDTO.getCompetenceId())
                .orElseGet(() -> {
                    CompetenceCandidat newSkill = new CompetenceCandidat();
                    newSkill.setCandidat(candidatRepository.findById(candidatId)
                            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found")));
                    newSkill.setCompetence(competence);
                    return newSkill;
                });

        competenceCandidat.setNiveau(skillDTO.getNiveau());
        competenceCandidatRepository.save(competenceCandidat);
    }

    @Transactional
    public void removeSkill(Long candidatId, Long competenceId) {
        CompetenceCandidat competenceCandidat = competenceCandidatRepository
                .findByCandidatIdAndCompetenceId(candidatId, competenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for this candidate"));
        competenceCandidatRepository.delete(competenceCandidat);
    }
}
