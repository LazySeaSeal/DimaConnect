package com.recrutement.app.repository;

import com.recrutement.app.model.PackPremium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackPremiumRepository extends JpaRepository<PackPremium, Long> {
    
    Optional<PackPremium> findByNom(String nom);
    
    List<PackPremium> findByPrixLessThanEqual(Float prix);
    
    List<PackPremium> findByDureeJoursGreaterThanEqual(Integer dureeJours);
    
    List<PackPremium> findByNombreOffresMaxGreaterThanEqual(Integer nombreOffresMax);
}
