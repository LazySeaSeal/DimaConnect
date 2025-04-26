package com.recrutement.app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrepriseInscriptionDto {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmerMotDePasse;

    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    private String description;

    @Pattern(regexp = "^[0-9]{14}$", message = "Le SIRET doit contenir 14 chiffres")
    private String siret;

    @URL(message = "L'URL du site doit être valide")
    private String urlSite;

    @Min(value = 1, message = "Le nombre d'employés doit être au moins 1")
    private Integer nombreEmployes;

    @Size(max = 100, message = "Le secteur d'activité ne peut pas dépasser 100 caractères")
    private String secteurActivite;
}