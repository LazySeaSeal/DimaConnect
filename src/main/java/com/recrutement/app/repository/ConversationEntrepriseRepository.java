package com.recrutement.app.repository;

import com.recrutement.app.model.ConversationEntreprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationEntrepriseRepository extends JpaRepository<ConversationEntreprise, Long> {
    
    @Query("SELECT c FROM ConversationEntreprise c WHERE " +
           "(c.entreprise1Id = :entreprise1Id AND c.entreprise2Id = :entreprise2Id) OR " +
           "(c.entreprise1Id = :entreprise2Id AND c.entreprise2Id = :entreprise1Id)")
    ConversationEntreprise findByEntreprises(Long entreprise1Id, Long entreprise2Id);
    
    @Query("SELECT c FROM ConversationEntreprise c WHERE " +
           "c.statut = 'ACTIVE' AND (c.entreprise1Id = :entrepriseId OR c.entreprise2Id = :entrepriseId) " +
           "ORDER BY c.derniereMiseAJour DESC")
    Page<ConversationEntreprise> findByEntrepriseId(Long entrepriseId, Pageable pageable);
}