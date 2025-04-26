package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer un commentaire d'entreprise sur une publication d'entreprise")
public class CommentaireEntreprisePublicationEntrepriseRequest {
    @Schema(description = "ID de l'entreprise qui fait le commentaire", required = true)
    private Long entrepriseId;
    
    @Schema(description = "ID de la publication de l'entreprise", required = true)
    private Long publicationEntrepriseId;
    
    @Schema(description = "Contenu du commentaire", required = true)
    private String contenu;
}