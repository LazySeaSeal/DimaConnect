package com.recrutement.app.dto;

import com.recrutement.app.model.enums.StatutContact;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Profile viewer information")
public class ProfileViewerDTO {
    private Long entrepriseId;
    private String entrepriseNom;
    private LocalDate dateConnexion;
    private StatutContact statut;
}