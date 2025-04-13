package com.recrutement.app.repository;

import com.recrutement.app.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    
    Optional<Competence> findByNom(String nom);
    
    boolean existsByNom(String nom);
    
    List<Competence> findByCategorie(String categorie);
}
