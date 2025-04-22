package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Candidate registration request")
public class CandidatRegistrationDTO {
    @NotBlank
    @Email
    @Schema(description = "Candidate's email", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 6)
    @Schema(description = "Password (min 6 characters)", example = "password123")
    private String motDePasse;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "Last name", example = "Doe")
    private String nom;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "First name", example = "John")
    private String prenom;
}