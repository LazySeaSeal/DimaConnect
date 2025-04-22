package com.recrutement.app.repository;

import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.ContactCandidat;
import com.recrutement.app.model.Employe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT c.receiver FROM ContactCandidat c " +
            "WHERE c.sender = :candidat AND c.receiver IN " +
            "(SELECT e.candidat FROM Employe e WHERE e.entreprise = :entreprise)")
    List<Candidat> findContactsInEntreprise(@Param("candidat") Candidat candidat, @Param("entreprise") Entreprise entreprise);
}
