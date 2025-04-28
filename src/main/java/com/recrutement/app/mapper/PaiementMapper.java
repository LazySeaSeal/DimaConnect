package com.recrutement.app.mapper;

import com.recrutement.app.dto.PaiementDTO;
import com.recrutement.app.model.Paiement;
import com.recrutement.app.model.Entreprise;

public class PaiementMapper {

    public static PaiementDTO toDTO(Paiement paiement) {
        return new PaiementDTO(
                paiement.getId(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getEntreprise() != null ? paiement.getEntreprise().getId() : null
        );
    }

    public static Paiement toEntity(PaiementDTO dto, Entreprise entreprise) {
        Paiement paiement = new Paiement();
        paiement.setId(dto.getId());
        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setEntreprise(entreprise);
        return paiement;
    }
}
