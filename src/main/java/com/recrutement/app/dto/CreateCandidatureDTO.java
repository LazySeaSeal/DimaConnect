package com.recrutement.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCandidatureDTO {

    @NotNull(message = "L'ID du candidat est obligatoire")
    private Long candidatId;

    @NotNull(message = "L'ID de l'offre d'emploi est obligatoire")
    private Long offreEmploiId;

    private String lettreMotivation; // Optionnel : Lettrée de motivation
}