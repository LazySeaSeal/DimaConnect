package com.recrutement.app.dto;

import com.recrutement.app.model.enums.TypeStatistique;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StatistiqueDTO {
    private Long id;
    private TypeStatistique type;
    private String periode;
    private LocalDate dateCreation;
    private Boolean estLue;
    private Long entrepriseId;
}
