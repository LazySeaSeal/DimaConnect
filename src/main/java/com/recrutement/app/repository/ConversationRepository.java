package com.recrutement.app.repository;

import com.recrutement.app.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE c.candidatId = :candidatId")
    Page<Conversation> findByCandidatId(@Param("candidatId") Long candidatId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE c.entrepriseId = :entrepriseId")
    Page<Conversation> findByEntrepriseId(@Param("entrepriseId") Long entrepriseId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE c.candidatId = :candidatId AND c.entrepriseId = :entrepriseId")
    Conversation findByCandidatIdAndEntrepriseId(@Param("candidatId") Long candidatId, 
                                               @Param("entrepriseId") Long entrepriseId);
}