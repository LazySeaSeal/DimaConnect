package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeEntretien;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EntretienResponseDTO {

    private Long id;
    private String candidatNom; // men Candidature->Candidat
    private TypeEntretien typeEntretien;
    private LocalDate date;
    private Integer duree;
    private String lienVisio;
    private Boolean estVisioConference;
    private String evaluateurNom; // men  Employe
}