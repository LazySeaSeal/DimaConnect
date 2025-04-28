package com.recrutement.app.repository;

import com.recrutement.app.model.CommentaireCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentaireCandidatRepository extends JpaRepository<CommentaireCandidat, Long> {
    List<CommentaireCandidat> findByCandidatId(Long candidatId);
    CommentaireCandidat findByIdAndCandidatId(Long id, Long candidatId);
}