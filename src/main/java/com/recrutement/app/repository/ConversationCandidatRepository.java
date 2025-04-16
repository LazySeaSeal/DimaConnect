package com.recrutement.app.repository;

import com.recrutement.app.model.ConversationCandidat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationCandidatRepository extends JpaRepository<ConversationCandidat, Long> {
    ConversationCandidat findByCandidat1IdAndCandidat2Id(Long candidat1Id, Long candidat2Id);
    Page<ConversationCandidat> findByCandidat1IdOrCandidat2Id(Long candidat1Id, Long candidat2Id, Pageable pageable);
}