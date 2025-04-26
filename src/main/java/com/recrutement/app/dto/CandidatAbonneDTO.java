package com.recrutement.app.dto;

import com.recrutement.app.model.Candidat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatAbonneDTO {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String shortBio;
    private LocalDate dateAbonnement;

    public static CandidatAbonneDTO fromCandidat(Candidat candidat, LocalDate dateAbonnement) {
        CandidatAbonneDTO dto = new CandidatAbonneDTO();
        dto.setId(candidat.getId());
        dto.setEmail(candidat.getEmail());
        dto.setNom(candidat.getNom());
        dto.setPrenom(candidat.getPrenom());
        dto.setTelephone(candidat.getTelephone());
        dto.setShortBio(candidat.getShortBio());
        dto.setDateAbonnement(dateAbonnement);
        return dto;
    }
}