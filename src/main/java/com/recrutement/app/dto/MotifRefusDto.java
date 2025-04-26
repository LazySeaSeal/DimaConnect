package com.recrutement.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotifRefusDto {

    @NotBlank(message = "Le motif de refus est obligatoire")
    private String motif;
}