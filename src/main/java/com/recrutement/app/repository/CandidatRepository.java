package com.recrutement.app.repository;

import com.recrutement.app.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    
    Optional<Candidat> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
