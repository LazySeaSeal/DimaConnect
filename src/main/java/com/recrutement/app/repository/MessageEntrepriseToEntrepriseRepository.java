package com.recrutement.app.repository;

import com.recrutement.app.model.MessageEntrepriseToEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageEntrepriseToEntrepriseRepository extends JpaRepository<MessageEntrepriseToEntreprise, Long> {
    List<MessageEntrepriseToEntreprise> findByConversationIdAndDestinataireIdAndEstLuFalse(Long conversationId, Long destinataireId);
}