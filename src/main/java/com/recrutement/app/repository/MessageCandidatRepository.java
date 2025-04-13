package com.recrutement.app.repository;

import com.recrutement.app.model.MessageCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MessageCandidatRepository extends JpaRepository<MessageCandidat, Long> {
    
    List<MessageCandidat> findByCandidatId(Long candidatId);
    
    List<MessageCandidat> findByEntrepriseId(Long entrepriseId);
    
    List<MessageCandidat> findByDateEnvoiAfter(LocalDate date);
    
    List<MessageCandidat> findByEstLuFalse();
    
    List<MessageCandidat> findByCandidatIdAndEntrepriseId(Long candidatId, Long entrepriseId);
}
