package com.recrutement.app.repository;

import com.recrutement.app.model.Abonnement;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    boolean existsByCandidatAndEntreprise(Candidat candidat, Entreprise entreprise);
    List<Abonnement> findByCandidatId(Long candidatId);
}
