package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCandidatDTO {
    private Long id;
    private Long candidatId;
    private TypeNotification type;
    private String contenu;
    private LocalDate dateCreation;
    private Boolean estLue;
    private String itemReference;
}