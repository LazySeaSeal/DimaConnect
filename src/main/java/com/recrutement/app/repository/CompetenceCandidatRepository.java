package com.recrutement.app.repository;

import com.recrutement.app.model.CompetenceCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceCandidatRepository extends JpaRepository<CompetenceCandidat, Long> {
    
    List<CompetenceCandidat> findByCandidatId(Long candidatId);
    
    List<CompetenceCandidat> findByCompetenceId(Long competenceId);
    
    List<CompetenceCandidat> findByNiveauGreaterThanEqual(Integer niveau);
    
    CompetenceCandidat findByCandidatIdAndCompetenceId(Long candidatId, Long competenceId);
}
