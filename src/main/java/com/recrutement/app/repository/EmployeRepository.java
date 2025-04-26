package com.recrutement.app.repository;

import com.recrutement.app.model.Employe;
import com.recrutement.app.model.enums.RoleEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    
    List<Employe> findByEntrepriseId(Long entrepriseId);
    
    List<Employe> findByNomContainingOrPrenomContaining(String nom, String prenom);
    List<Employe> findByEntrepriseIdAndRole(Long entrepriseId, RoleEmploye role);

    Optional<Employe> findByEmail(String email);
    boolean existsByEmail(String email);  // Add this method
    Optional<Employe> findByEmailAndEntrepriseIdAndRole(String email, Long entrepriseId, RoleEmploye role);


}
