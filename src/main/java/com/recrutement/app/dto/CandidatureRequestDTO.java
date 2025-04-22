package com.recrutement.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CandidatureRequestDTO {

    @NotNull
    private Long candidatId;

    @NotNull
    private Long offreId;

    @Size(max = 5000)
    private String lettreMotivation;
}
