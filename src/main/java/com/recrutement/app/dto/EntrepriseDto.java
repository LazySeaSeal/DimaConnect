package com.recrutement.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class EntrepriseDto {
    private Long id;
    private String nom;
    private String email;
    private String description;
    private String secteurActivite;
    private String urlSite;
    private List<OffreEmploiDto> offres;

    public void setOffres(List<OffreEmploiDto> offres) {
        this.offres = offres;
    }
}
