
package com.recrutement.app.repository;

import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.model.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationCandidatRepository extends JpaRepository<NotificationCandidat, Long> {
    List<NotificationCandidat> findByType(TypeNotification type);
    List<NotificationCandidat> findByTypeAndEstLue(TypeNotification type, Boolean estLue);
    List<NotificationCandidat> findByCandidatIdAndEstLue(Long candidatId, Boolean estLue);
    List<NotificationCandidat> findByCandidatId(Long candidatId);
    
    @Modifying
    @Transactional
    @Query("UPDATE NotificationCandidat n SET n.estLue = true WHERE n.candidat.id = :candidatId")
    void marquerToutesCommeLues(@Param("candidatId") Long candidatId);
}