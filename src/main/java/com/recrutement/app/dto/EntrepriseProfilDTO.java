package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrepriseProfilDTO {
    private Long id;
    private String nom;
    private String description;
    private String secteurActivite;
    private String urlSite;
    private Integer nombreEmployes;
    private LocalDate dateCreation;
    private Boolean estVerifiee;
    private Boolean estPremium;
    private List<EvenementDTO> evenements;
    private List<PublicationDTO> publications;
    private List<CommentaireDTO> commentaires;
}
