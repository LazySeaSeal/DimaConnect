package com.recrutement.app.repository;

import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long> {

    List<OffreEmploi> findByEntrepriseAndEstActiveTrue(Entreprise entreprise);

    List<OffreEmploi> findByEntreprise(Entreprise entreprise);

    List<OffreEmploi> findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titre, String description);
    List<OffreEmploi> findByLocalisationContainingIgnoreCase(String location);

    List<OffreEmploi> findByEstActiveTrue();
}
