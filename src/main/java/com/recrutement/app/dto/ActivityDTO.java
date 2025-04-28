package com.recrutement.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Candidate activity")
public class ActivityDTO {
    private Long id;
    private String type;
    private String content;
    private LocalDate date;
    private Long relatedId;
    private Integer likes;
    private Boolean isRead;
}