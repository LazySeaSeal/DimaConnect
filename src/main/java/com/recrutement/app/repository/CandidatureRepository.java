package com.recrutement.app.repository;

import com.recrutement.app.model.Candidature;
import com.recrutement.app.model.enums.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    
    List<Candidature> findByCandidatId(Long candidatId);
    
    List<Candidature> findByOffreEmploiId(Long offreEmploiId);
    
    List<Candidature> findByStatut(StatutCandidature statut);
    
    List<Candidature> findByCandidatIdAndStatut(Long candidatId, StatutCandidature statut);
    
    List<Candidature> findByOffreEmploiIdAndStatut(Long offreEmploiId, StatutCandidature statut);
}
