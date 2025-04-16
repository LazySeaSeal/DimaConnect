package com.recrutement.app.repository;

import com.recrutement.app.model.ConversationEntreprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationEntrepriseRepository extends JpaRepository<ConversationEntreprise, Long> {
    ConversationEntreprise findByEntreprise1IdAndEntreprise2Id(Long entreprise1Id, Long entreprise2Id);
    Page<ConversationEntreprise> findByEntreprise1IdOrEntreprise2Id(Long entreprise1Id, Long entreprise2Id, Pageable pageable);
}