package com.recrutement.app.dto;

import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.StatutOffre;
import com.recrutement.app.model.enums.TypeContrat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for simplified job offer information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreEmploiDTO {
    private Long id;
    private String titre;
    private String description;
    private TypeContrat typeContrat;
    private String localisation;
    private String salaire;
    private LocalDate dateExpiration;
    private LocalDate dateCreation;
    private LocalDate dateValidation;
    private Boolean estActive;
    private StatutOffre statut;
    private Integer niveauExpertise;

    // Company info
    private Long entrepriseId;
    private String entrepriseNom;

    // Creator info (optional)
    private String createurNom;

    // List of competence names (optional)
    private List<String> competences;

    /**
     * Factory method to create DTO from entity
     */
    public static OffreEmploiDTO fromEntity(OffreEmploi offreEmploi) {
        OffreEmploiDTO dto = new OffreEmploiDTO();

        // Basic job offer info
        dto.setId(offreEmploi.getId());
        dto.setTitre(offreEmploi.getTitre());
        dto.setDescription(offreEmploi.getDescription());
        dto.setTypeContrat(offreEmploi.getTypeContrat());
        dto.setLocalisation(offreEmploi.getLocalisation());
        dto.setSalaire(offreEmploi.getSalaire());
        dto.setDateExpiration(offreEmploi.getDateExpiration());
        dto.setDateCreation(offreEmploi.getDateCreation());
        dto.setDateValidation(offreEmploi.getDateValidation());
        dto.setEstActive(offreEmploi.getEstActive());
        dto.setStatut(offreEmploi.getStatut());
        dto.setNiveauExpertise(offreEmploi.getNiveauExpertise());

        // Company info
        if (offreEmploi.getEntreprise() != null) {
            dto.setEntrepriseId(offreEmploi.getEntreprise().getId());
            dto.setEntrepriseNom(offreEmploi.getEntreprise().getNom());
        }

        // Creator info
        if (offreEmploi.getCreateur() != null) {
            dto.setCreateurNom(offreEmploi.getCreateur().getNom());
        }

        // Competences (if needed)
        if (offreEmploi.getCompetences() != null && !offreEmploi.getCompetences().isEmpty()) {
            dto.setCompetences(offreEmploi.getCompetences().stream()
                    .map(comp -> comp.getNom())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}