package com.recrutement.app.repository;

import com.recrutement.app.model.CommentaireCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommentaireCandidatRepository extends JpaRepository<CommentaireCandidat, Long> {
    
    List<CommentaireCandidat> findByCandidatId(Long candidatId);
    
    List<CommentaireCandidat> findByPublicationEntrepriseId(Long publicationEntrepriseId);
    
    List<CommentaireCandidat> findByDateCreationAfter(LocalDate date);
    
    List<CommentaireCandidat> findByEstLuFalse();
}
