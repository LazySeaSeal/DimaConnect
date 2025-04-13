package com.recrutement.app.repository;

import com.recrutement.app.model.Entretien;
import com.recrutement.app.model.enums.ResultatEntretien;
import com.recrutement.app.model.enums.TypeEntretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Long> {
    
    List<Entretien> findByCandidatureId(Long candidatureId);
    
    List<Entretien> findByEvaluateurId(Long evaluateurId);
    
    List<Entretien> findByPostulantId(Long postulantId);
    
    List<Entretien> findByType(TypeEntretien type);
    
    List<Entretien> findByResultat(ResultatEntretien resultat);
    
    List<Entretien> findByDateAfter(LocalDate date);
    
    List<Entretien> findByDateBefore(LocalDate date);
}
