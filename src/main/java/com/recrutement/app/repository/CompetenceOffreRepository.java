
package com.recrutement.app.repository;

import com.recrutement.app.model.CompetenceOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompetenceOffreRepository extends JpaRepository<CompetenceOffre, Long> {

    Set<CompetenceOffre> findByOffreEmploiId(Long offreId);

    @Query("SELECT co FROM CompetenceOffre co WHERE co.offreEmploi.id = :offreId AND co.competence.id = :competenceId")
    CompetenceOffre findByOffreIdAndCompetenceId(@Param("offreId") Long offreId, @Param("competenceId") Long competenceId);

    void deleteByOffreEmploiId(Long offreId);
}