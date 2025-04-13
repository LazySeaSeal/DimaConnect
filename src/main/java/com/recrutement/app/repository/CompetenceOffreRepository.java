package com.recrutement.app.repository;

import com.recrutement.app.model.CompetenceOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceOffreRepository extends JpaRepository<CompetenceOffre, Long> {
    
    List<CompetenceOffre> findByOffreEmploiId(Long offreEmploiId);
    
    List<CompetenceOffre> findByCompetenceId(Long competenceId);
    
    List<CompetenceOffre> findByNiveauGreaterThanEqual(Integer niveau);
    
    List<CompetenceOffre> findByEstObligatoireTrue();
    
    CompetenceOffre findByOffreEmploiIdAndCompetenceId(Long offreEmploiId, Long competenceId);
}
