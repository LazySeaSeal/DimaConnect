package com.recrutement.app.repository;

import com.recrutement.app.model.ConversationCandidat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationCandidatRepository extends JpaRepository<ConversationCandidat, Long> {
    
    @Query("SELECT c FROM ConversationCandidat c WHERE " +
           "(c.candidat1Id = :candidat1Id AND c.candidat2Id = :candidat2Id) OR " +
           "(c.candidat1Id = :candidat2Id AND c.candidat2Id = :candidat1Id)")
    ConversationCandidat findByCandidats(Long candidat1Id, Long candidat2Id);
    
    @Query("SELECT c FROM ConversationCandidat c WHERE " +
           "c.statut = 'ACTIVE' AND (c.candidat1Id = :candidatId OR c.candidat2Id = :candidatId) " +
           "ORDER BY c.derniereMiseAJour DESC")
    Page<ConversationCandidat> findByCandidatId(Long candidatId, Pageable pageable);
}