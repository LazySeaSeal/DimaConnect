package com.recrutement.app.dto;

import lombok.Data;

@Data
public class CommentaireCandidatDTO {
    private Long publicationId;
    private String contenu;
    private Boolean estLu;
}