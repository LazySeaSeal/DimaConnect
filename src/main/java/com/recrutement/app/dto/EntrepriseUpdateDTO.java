package com.recrutement.app.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrepriseUpdateDTO {

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    private String description;

    @Size(max = 100, message = "Le secteur d'activité ne peut pas dépasser 100 caractères")
    private String secteurActivite;

    @URL(message = "L'URL du site doit être valide")
    private String urlSite;

    @Min(value = 1, message = "Le nombre d'employés doit être au moins 1")
    private Integer nombreEmployes;

    @Pattern(regexp = "^[0-9]{14}$", message = "Le SIRET doit contenir 14 chiffres")
    private String siret;

    private Boolean estVerifiee;

    private Boolean estPremium;

    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate dateExpiration;
}