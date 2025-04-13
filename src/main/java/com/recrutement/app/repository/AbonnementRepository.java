package com.recrutement.app.repository;

import com.recrutement.app.model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    
    List<Abonnement> findByCandidatId(Long candidatId);
    
    List<Abonnement> findByEntrepriseId(Long entrepriseId);
    
    List<Abonnement> findByDateAbonnementAfter(LocalDate date);
    
    Abonnement findByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
}
