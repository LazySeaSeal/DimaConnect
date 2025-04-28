package com.recrutement.app.mapper;

import com.recrutement.app.dto.StatistiqueDTO;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.Statistique;

public class StatistiqueMapper {

    public static StatistiqueDTO toDTO(Statistique statistique) {
        if (statistique == null) return null;

        StatistiqueDTO dto = new StatistiqueDTO();
        dto.setId(statistique.getId());
        dto.setType(statistique.getType());
        dto.setPeriode(statistique.getPeriode());
        dto.setDateCreation(statistique.getDateCreation());
        dto.setEstLue(statistique.getEstLue());
        dto.setEntrepriseId(statistique.getEntreprise() != null ? statistique.getEntreprise().getId() : null);
        return dto;
    }

    public static Statistique toEntity(StatistiqueDTO dto, Entreprise entreprise) {
        if (dto == null) return null;

        Statistique statistique = new Statistique();
        statistique.setId(dto.getId());
        statistique.setType(dto.getType());
        statistique.setPeriode(dto.getPeriode());
        statistique.setDateCreation(dto.getDateCreation() != null ? dto.getDateCreation() : statistique.getDateCreation());
        statistique.setEstLue(dto.getEstLue() != null ? dto.getEstLue() : false);
        statistique.setEntreprise(entreprise);
        return statistique;
    }
}
