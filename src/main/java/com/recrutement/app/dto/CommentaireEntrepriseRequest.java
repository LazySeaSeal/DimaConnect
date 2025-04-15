package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireEntrepriseRequest {
    private Long entrepriseId;
    private Long publicationCandidatId;
    private String contenu;
}