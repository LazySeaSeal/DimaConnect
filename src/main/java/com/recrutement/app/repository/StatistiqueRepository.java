package com.recrutement.app.repository;

import com.recrutement.app.model.Statistique;
import com.recrutement.app.model.enums.TypeStatistique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatistiqueRepository extends JpaRepository<Statistique, Long> {
    
    List<Statistique> findByEntrepriseId(Long entrepriseId);
    
    List<Statistique> findByType(TypeStatistique type);
    
    List<Statistique> findByDateCreationAfter(LocalDate date);
    
    List<Statistique> findByEstLueFalse();
    
    List<Statistique> findByPeriode(String periode);
}
