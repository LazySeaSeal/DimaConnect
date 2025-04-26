package com.recrutement.app.repository;

import com.recrutement.app.model.Abonnement;
import com.recrutement.app.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    
    List<Abonnement> findByCandidatId(Long candidatId);
    List<Abonnement> findByEntreprise(Entreprise entreprise);
    List<Abonnement> findByEntrepriseId(Long entrepriseId);
    boolean existsByEntrepriseIdAndCandidatId(Long entrepriseId, Long candidatId);
    List<Abonnement> findByDateAbonnementAfter(LocalDate date);
    
    Abonnement findByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
}
