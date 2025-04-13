package com.recrutement.app.repository;

import com.recrutement.app.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    
    List<Test> findByCreateurId(Long createurId);
    
    List<Test> findByTitreContaining(String titre);
    
    List<Test> findByDureeMinutesLessThanEqual(Integer dureeMinutes);
    
    List<Test> findByPointsReussiteGreaterThanEqual(Float pointsReussite);
}
