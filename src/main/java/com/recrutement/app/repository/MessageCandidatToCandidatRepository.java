package com.recrutement.app.repository;

import com.recrutement.app.model.MessageCandidatToCandidat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageCandidatToCandidatRepository extends JpaRepository<MessageCandidatToCandidat, Long> {
    List<MessageCandidatToCandidat> findByConversationIdAndDestinataireIdAndEstLuFalse(Long conversationId, Long destinataireId);
}