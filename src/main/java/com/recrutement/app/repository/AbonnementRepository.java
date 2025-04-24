package com.recrutement.app.repository;

import com.recrutement.app.model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    
    List<Abonnement> findByCandidatId(Long candidatId);
    
    List<Abonnement> findByEntrepriseId(Long entrepriseId);
    
    List<Abonnement> findByDateAbonnementAfter(LocalDate date);
    
    Optional<Abonnement> findByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
    
    // Ajoutez cette méthode pour vérifier l'existence d'un abonnement
    boolean existsByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
}