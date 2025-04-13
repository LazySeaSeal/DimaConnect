package com.recrutement.app.repository;

import com.recrutement.app.model.CommentaireEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommentaireEntrepriseRepository extends JpaRepository<CommentaireEntreprise, Long> {
    
    List<CommentaireEntreprise> findByEntrepriseId(Long entrepriseId);
    
    List<CommentaireEntreprise> findByPublicationCandidatId(Long publicationCandidatId);
    
    List<CommentaireEntreprise> findByDateCreationAfter(LocalDate date);
    
    List<CommentaireEntreprise> findByEstLuFalse();
}
