package com.recrutement.app.repository;

import com.recrutement.app.model.MessageEntreprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MessageEntrepriseRepository extends JpaRepository<MessageEntreprise, Long> {
    List<MessageEntreprise> findByCandidatId(Long candidatId);
    List<MessageEntreprise> findByEntrepriseId(Long entrepriseId);
    List<MessageEntreprise> findByDateEnvoiAfter(LocalDate date);
    List<MessageEntreprise> findByEstLuFalse();
    List<MessageEntreprise> findByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
    
    // Nouvelles méthodes ajoutées
    Page<MessageEntreprise> findByConversationId(Long conversationId, Pageable pageable);
    List<MessageEntreprise> findByConversationIdAndCandidatIdAndEstLuFalse(Long conversationId, Long candidatId);
}