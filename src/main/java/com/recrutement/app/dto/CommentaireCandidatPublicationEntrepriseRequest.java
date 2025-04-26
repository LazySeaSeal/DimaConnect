package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer un commentaire de candidat sur une publication d'entreprise")
public class CommentaireCandidatPublicationEntrepriseRequest {
    @Schema(description = "ID du candidat qui fait le commentaire", required = true)
    private Long candidatId;
    
    @Schema(description = "ID de la publication de l'entreprise", required = true)
    private Long publicationEntrepriseId;
    
    @Schema(description = "Contenu du commentaire", required = true)
    private String contenu;
}