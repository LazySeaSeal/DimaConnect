package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeEntretien;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
public class EntretienRequestDTO {

    @NotNull(message = "La candidature ID est obligatoire")
    private Long candidatureId;

    @NotNull(message = "Le type d'entretien est obligatoire")
    private TypeEntretien typeEntretien;

    @NotNull(message = "La date est obligatoire")
    @Future(message = "La date doit être dans le futur")
    private LocalDate date;

    @Min(value = 15, message = "La durée minimale est de 15 minutes")
    @Max(value = 240, message = "La durée maximale est de 240 minutes")
    private Integer duree; // en minutes

    @URL(message = "Lien Visio doit être une URL valide")
    private String lienVisio;

    @NotNull(message = "L'évaluateur ID est obligatoire")
    private Long evaluateurId;

    private Boolean estVisioConference;
}