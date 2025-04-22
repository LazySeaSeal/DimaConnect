package com.recrutement.app.dto;

import com.recrutement.app.model.enums.StatutContact;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Candidate Profile DTO")
public class CandidatProfileDTO {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;

    @Schema(description = "Short bio/introduction of the candidate")
    private String shortBio;

    @Schema(description = "Professional experience")
    private String experienceProfessionnelle;

    @Schema(description = "Education background")
    private String formation;



    @Schema(description = "Licenses and certifications")
    private String licencesEtCertifications;

    @Schema(description = "List of skills with ratings")
    private List<CompetenceRatingDTO> competences;

    @Schema(description = "Profiles that viewed this candidate")
    private Set<ProfileViewerDTO> profileViewers;

    @Schema(description = "List of activities")
    private List<ActivityDTO> activities;

    private String cv;
    private boolean emailConfirmed;
}