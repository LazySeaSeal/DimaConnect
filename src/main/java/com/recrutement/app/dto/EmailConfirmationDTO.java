package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Email confirmation request")
public class EmailConfirmationDTO {
    @NotBlank
    @Email
    @Schema(description = "Candidate's email", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Confirmation token", example = "abc123")
    private String token;
}