package com.recrutement.app.repository;

import com.recrutement.app.model.AbonnementPremium;
import com.recrutement.app.model.enums.TypeAbonnementRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementPremiumRepository extends JpaRepository<AbonnementPremium, Long> {
    
    List<AbonnementPremium> findByEntrepriseId(Long entrepriseId);
    
    List<AbonnementPremium> findByPackId(Long packId);
    
    List<AbonnementPremium> findByEstActifTrue();
    
    List<AbonnementPremium> findByDateFinBefore(LocalDate date);
    
    List<AbonnementPremium> findByDateFinAfter(LocalDate date);
    
    List<AbonnementPremium> findByRef(TypeAbonnementRef ref);
}
