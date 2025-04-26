
package com.recrutement.app.repository;

import com.recrutement.app.model.CompetenceOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CompetenceOffreRepository extends JpaRepository<CompetenceOffre, Long> {

    Set<CompetenceOffre> findByOffreEmploiId(Long offreId);
    Optional<CompetenceOffre> findByCompetenceIdAndOffreEmploiId(Long competenceId, Long offreEmploiId);
    void deleteByOffreEmploiId(Long offreId);
}