package com.recrutement.app.repository;

import com.recrutement.app.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.recrutement.app.model.Competence;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    List<Candidat> findByEmail(String email);
    Optional<Candidat> findAllByEmail(String email);
    
    boolean existsByEmail(String email);

    @Query("SELECT c FROM Candidat c JOIN c.competences comp WHERE comp IN :competences")
    List<Candidat> findCandidatsWithCompetences(@Param("competences") Set<Competence> competences);

    @Query("SELECT c FROM Candidat c JOIN c.competences comp " +
            "WHERE comp.id IN :competencesIds " +
            "GROUP BY c.id " +
            "HAVING COUNT(DISTINCT comp.id) = (SELECT COUNT(DISTINCT id) FROM Competence WHERE id IN :competencesIds)")
    List<Candidat> findCandidatsWithRequiredCompetences(@Param("competencesIds") Set<Long> competencesIds);
}
