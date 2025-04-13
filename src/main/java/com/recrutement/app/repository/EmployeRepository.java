package com.recrutement.app.repository;

import com.recrutement.app.model.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    
    List<Employe> findByEntrepriseId(Long entrepriseId);
    
    List<Employe> findByNomContainingOrPrenomContaining(String nom, String prenom);
}
