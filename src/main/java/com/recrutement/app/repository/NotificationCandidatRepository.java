package com.recrutement.app.repository;

import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.model.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationCandidatRepository extends JpaRepository<NotificationCandidat, Long> {
    
    List<NotificationCandidat> findByCandidatId(Long candidatId);
    
    List<NotificationCandidat> findByType(TypeNotification type);
    
    List<NotificationCandidat> findByDateCreationAfter(LocalDate date);
    
    List<NotificationCandidat> findByEstLueFalse();

    List<NotificationCandidat> findByCandidatIdOrderByDateCreationDesc(Long candidatId);

    List<NotificationCandidat> findByCandidatIdAndEstLueFalseOrderByDateCreationDesc(Long candidatId);

    long countByCandidatIdAndEstLueFalse(Long candidatId);
}
