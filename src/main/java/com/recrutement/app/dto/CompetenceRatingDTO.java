package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Skill with rating information")
public class CompetenceRatingDTO {
    private Long competenceId;
    private String nom;
    private String categorie;

    @Schema(description = "Rating level (1-5)", minimum = "1", maximum = "5")
    private Integer niveau;
}