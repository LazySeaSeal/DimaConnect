package com.recrutement.app.repository;
import java.util.Optional;

import com.recrutement.app.model.ContactCandidat;
import com.recrutement.app.model.enums.StatutContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContactCandidatRepository extends JpaRepository<ContactCandidat, Long> {
    List<ContactCandidat> findByReceiverIdAndStatut(Long receiverId, StatutContact statut);
    List<ContactCandidat> findBySenderIdAndStatut(Long senderId, StatutContact statut);
    List<ContactCandidat> findByReceiverId(Long receiverId);
    List<ContactCandidat> findBySenderIdOrReceiverIdAndStatut(Long senderId, Long receiverId, StatutContact statut);
    Optional<ContactCandidat> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
}