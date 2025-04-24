package com.recrutement.app.repository;

import com.recrutement.app.model.NotificationEntreprise;
import com.recrutement.app.model.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationEntrepriseRepository extends JpaRepository<NotificationEntreprise, Long> {
    
    List<NotificationEntreprise> findByEntrepriseId(Long entrepriseId);
    
    // Ajoutez cette méthode pour résoudre l'erreur
    List<NotificationEntreprise> findByEntrepriseIdAndEstLueFalse(Long entrepriseId);
    
    List<NotificationEntreprise> findByType(TypeNotification type);
    
    List<NotificationEntreprise> findByDateCreationAfter(LocalDate date);
    
    List<NotificationEntreprise> findByEstLueFalse();
}