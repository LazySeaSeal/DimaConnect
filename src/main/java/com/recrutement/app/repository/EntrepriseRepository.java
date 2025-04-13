package com.recrutement.app.repository;

import com.recrutement.app.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    
    Optional<Entreprise> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<Entreprise> findBySiret(String siret);
    
    boolean existsBySiret(String siret);
}
