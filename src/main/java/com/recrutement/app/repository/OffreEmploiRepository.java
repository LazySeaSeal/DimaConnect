package com.recrutement.app.repository;

import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.TypeContrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long> {
    
    List<OffreEmploi> findByEntrepriseId(Long entrepriseId);
    
    List<OffreEmploi> findByTypeContrat(TypeContrat typeContrat);
    
    List<OffreEmploi> findByLocalisation(String localisation);
    
    List<OffreEmploi> findByEstActiveTrue();
    
    List<OffreEmploi> findByDateExpirationAfter(LocalDate date);
    
    @Query("SELECT o FROM OffreEmploi o WHERE o.titre LIKE %?1% OR o.description LIKE %?1%")
    List<OffreEmploi> searchByKeyword(String keyword);
}
