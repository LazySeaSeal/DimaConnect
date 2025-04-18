package com.recrutement.app.repository;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.NotificationCandidat;
import com.recrutement.app.model.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;public interface NotificationCandidatRepository extends JpaRepository<NotificationCandidat, Long> {
    // Méthodes existantes (à conserver)
    List<NotificationCandidat> findByType(TypeNotification type);
    List<NotificationCandidat> findByTypeAndEstLue(TypeNotification type, Boolean estLue);
    List<NotificationCandidat> findByCandidatIdAndEstLue(Long candidatId, Boolean estLue);
    List<NotificationCandidat> findByCandidatId(Long candidatId);
    
    @Modifying
    @Transactional
    @Query("UPDATE NotificationCandidat n SET n.estLue = true WHERE n.candidat.id = :candidatId")
    void marquerToutesCommeLues(@Param("candidatId") Long candidatId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationCandidat n SET n.estLue = true WHERE n.candidat.id = :candidatId")
    void updateAllNotificationsAsRead(@Param("candidatId") Long candidatId);

    boolean existsByCandidatIdAndItemReferenceAndType(Long candidatId, String itemReference, TypeNotification type);

    // Ajouts nécessaires
    boolean existsByCandidatAndItemReference(Candidat candidat, String itemReference);
    
    List<NotificationCandidat> findByCandidatIdAndType(Long candidatId, TypeNotification type);
    
    @Query("SELECT n FROM NotificationCandidat n WHERE n.candidat.id = :candidatId AND n.type = 'OFFRE' ORDER BY n.dateCreation DESC")
    List<NotificationCandidat> findOffresNotificationsByCandidat(@Param("candidatId") Long candidatId);
    
    long countByCandidatIdAndEstLueFalse(Long candidatId);
}