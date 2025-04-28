package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeMedia;
import lombok.Data;

@Data
public class PublicationCandidatDTO {
    private String contenu;
    private String mediaUrl;
    private TypeMedia typeMedia;
}