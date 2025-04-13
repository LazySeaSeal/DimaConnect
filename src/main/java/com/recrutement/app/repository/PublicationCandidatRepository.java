package com.recrutement.app.repository;

import com.recrutement.app.model.PublicationCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicationCandidatRepository extends JpaRepository<PublicationCandidat, Long> {
    
    List<PublicationCandidat> findByCandidatId(Long candidatId);
    
    List<PublicationCandidat> findByDatePublicationAfter(LocalDate date);
    
    List<PublicationCandidat> findByNombreLikesGreaterThan(Integer nombreLikes);
}
