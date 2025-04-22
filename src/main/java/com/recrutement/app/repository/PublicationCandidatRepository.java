package com.recrutement.app.repository;

import com.recrutement.app.model.PublicationCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PublicationCandidatRepository extends JpaRepository<PublicationCandidat, Long> {
    List<PublicationCandidat> findByCandidatId(Long candidatId);
    PublicationCandidat findByIdAndCandidatId(Long id, Long candidatId);
}