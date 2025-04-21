// PublicationRequest.java
package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeMedia;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class PublicationRequest {
    private Long ownerId; // candidatId or entrepriseId
    private String contenu;
    private String mediaUrl;
    private TypeMedia typeMedia;
}