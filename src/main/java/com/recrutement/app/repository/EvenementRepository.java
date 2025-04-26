package com.recrutement.app.repository;

import com.recrutement.app.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findByEntrepriseId(Long entrepriseId);

    List<Evenement> findByEntrepriseIdAndDateFinAfter(Long entrepriseId, LocalDateTime date);
}