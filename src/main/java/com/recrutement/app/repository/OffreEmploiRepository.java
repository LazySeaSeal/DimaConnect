package com.recrutement.app.repository;

import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.TypeContrat;
import com.recrutement.app.model.enums.StatutOffre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long> {

    List<OffreEmploi> findByEntrepriseIdAndStatut(Long entrepriseId, StatutOffre statut);

    @Query("SELECT DISTINCT o FROM OffreEmploi o LEFT JOIN o.competences c " +
            "WHERE (:titre IS NULL OR o.titre LIKE CONCAT('%', :titre, '%')) " +
            "AND (:datePublication IS NULL OR o.dateValidation >= :datePublication) " +
            "AND (:niveauExpertise IS NULL OR o.niveauExpertise = :niveauExpertise) " +
            "AND (:typeContrat IS NULL OR o.typeContrat = :typeContrat) " +
            "AND o.statut = 'VALIDEE' AND o.estActive = true " +
            "AND o.dateExpiration >= CURRENT_DATE")
    Page<OffreEmploi> rechercherOffres(
            @Param("titre") String titre,
            @Param("datePublication") LocalDate datePublication,
            @Param("niveauExpertise") Integer niveauExpertise,
            @Param("typeContrat") TypeContrat typeContrat,
            Pageable pageable);

    @Query("SELECT DISTINCT o FROM OffreEmploi o JOIN o.competences c " +
            "WHERE c.id IN :competenceIds " +
            "AND o.statut = 'VALIDEE' AND o.estActive = true " +
            "AND o.dateExpiration >= CURRENT_DATE")
    Page<OffreEmploi> rechercherParCompetences(
            @Param("competenceIds") List<Long> competenceIds,
            Pageable pageable);

    // ✅ Recherche avancée conservée ici
    @Query("SELECT DISTINCT o FROM OffreEmploi o LEFT JOIN o.competences c " +
            "WHERE (:titre IS NULL OR LOWER(o.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) " +
            "AND (:datePublication IS NULL OR o.dateValidation >= :datePublication) " +
            "AND (:niveauExpertise IS NULL OR o.niveauExpertise = :niveauExpertise) " +
            "AND (:typeContrat IS NULL OR o.typeContrat = :typeContrat) " +
            "AND (:salaireMini IS NULL OR CAST(SUBSTRING(o.salaire, 1, LOCATE('€', o.salaire) - 1) AS double) >= :salaireMini) " +
            "AND (:salaireMaxi IS NULL OR CAST(SUBSTRING(o.salaire, 1, LOCATE('€', o.salaire) - 1) AS double) <= :salaireMaxi) " +
            "AND (:localisation IS NULL OR LOWER(o.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))) " +
            "AND (COALESCE(:competenceIds, NULL) IS NULL OR c.id IN :competenceIds) " +
            "AND o.statut = 'VALIDEE' AND o.estActive = true " +
            "AND o.dateExpiration >= CURRENT_DATE")
    Page<OffreEmploi> rechercheAvancee(
            @Param("titre") String titre,
            @Param("datePublication") LocalDate datePublication,
            @Param("niveauExpertise") Integer niveauExpertise,
            @Param("typeContrat") TypeContrat typeContrat,
            @Param("salaireMini") Double salaireMini,
            @Param("salaireMaxi") Double salaireMaxi,
            @Param("localisation") String localisation,
            @Param("competenceIds") List<Long> competenceIds,
            Pageable pageable);
}
