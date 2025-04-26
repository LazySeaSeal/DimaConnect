package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer un commentaire de candidat sur une publication de candidat")
public class CommentaireCandidatPublicationCandidatRequest {
    @Schema(description = "ID du candidat qui fait le commentaire", required = true)
    private Long candidatId;
    
    @Schema(description = "ID de la publication du candidat", required = true)
    private Long publicationCandidatId;
    
    @Schema(description = "Contenu du commentaire", required = true)
    private String contenu;
}