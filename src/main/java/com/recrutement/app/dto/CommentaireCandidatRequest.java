package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaireCandidatRequest {
    private Long candidatId;
    private Long publicationEntrepriseId;
    private String contenu;
}