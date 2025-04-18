
package com.recrutement.app.repository;

import com.recrutement.app.model.ContactCandidat;
import com.recrutement.app.model.enums.StatutContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactCandidatRepository extends JpaRepository<ContactCandidat, Long> {
    List<ContactCandidat> findByStatut(StatutContact statut);
    List<ContactCandidat> findByReceiverIdAndStatut(Long receiverId, StatutContact statut);
    List<ContactCandidat> findBySenderIdAndStatut(Long senderId, StatutContact statut);
}