package com.recrutement.app.repository;

import com.recrutement.app.model.CompetenceCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetenceCandidatRepository extends JpaRepository<CompetenceCandidat, Long> {
    List<CompetenceCandidat> findByCandidatId(Long candidatId);
    boolean existsByCandidatIdAndCompetenceId(Long candidatId, Long competenceId);
    Optional<CompetenceCandidat> findByCandidatIdAndCompetenceId(Long candidatId, Long competenceId);
}