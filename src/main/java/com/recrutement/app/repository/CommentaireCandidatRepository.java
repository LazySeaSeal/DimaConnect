package com.recrutement.app.repository;

import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.model.PublicationEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireCandidatRepository extends JpaRepository<CommentaireCandidat, Long> {
    List<CommentaireCandidat> findByPublicationEntreprise(PublicationEntreprise publicationEntreprise);
    List<CommentaireCandidat> findByCandidat(Candidat candidat);
    List<CommentaireCandidat> findByPublicationEntrepriseIdAndEstLu(Long publicationEntrepriseId, Boolean estLu);
    List<CommentaireCandidat> findByPublicationCandidat(PublicationCandidat publicationCandidat);
    List<CommentaireCandidat> findByPublicationCandidatIdAndEstLu(Long publicationCandidatId, Boolean estLu);
}