package com.recrutement.app.dto;

import com.recrutement.app.model.enums.StatutCandidature;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CandidatureDTO {
    private Long candidatId;
    private Long offreEmploiId;
    private String lettreMotivation;
    private Float noteEvaluation;
    private StatutCandidature statut;
}