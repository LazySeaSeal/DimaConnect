package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireDTO {
    private Long id;
    private String contenu;
    private LocalDate dateCreation;
    private String auteurNom;
    private String auteurType; // "CANDIDAT" ou "ENTREPRISE"
}
