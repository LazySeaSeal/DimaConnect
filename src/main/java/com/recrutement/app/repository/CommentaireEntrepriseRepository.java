package com.recrutement.app.repository;

import com.recrutement.app.model.CommentaireEntreprise;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.model.PublicationEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentaireEntrepriseRepository extends JpaRepository<CommentaireEntreprise, Long> {
    List<CommentaireEntreprise> findByPublicationCandidat(PublicationCandidat publicationCandidat);
    List<CommentaireEntreprise> findByEntreprise(Entreprise entreprise);
    List<CommentaireEntreprise> findByPublicationCandidatIdAndEstLu(Long publicationCandidatId, Boolean estLu);
    List<CommentaireEntreprise> findByPublicationEntreprise(PublicationEntreprise publicationEntreprise);
    List<CommentaireEntreprise> findByPublicationEntrepriseIdAndEstLu(Long publicationEntrepriseId, Boolean estLu);
}