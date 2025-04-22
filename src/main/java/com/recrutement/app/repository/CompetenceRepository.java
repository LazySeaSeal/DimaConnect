package com.recrutement.app.repository;

import com.recrutement.app.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    List<Competence> findByCategorie(String categorie);
}