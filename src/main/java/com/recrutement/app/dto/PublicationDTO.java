package com.recrutement.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDTO {
    private Long id;
    private String contenu;
    private String mediaUrl;
    private String typeMedia;
    private LocalDate datePublication;
    private Integer nombreLikes;
    private List<CommentaireDTO> commentaires;
}
