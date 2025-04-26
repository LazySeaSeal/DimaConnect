package com.recrutement.app.model.enums;

public enum StatutOffre {
    EN_ATTENTE,    // Offre créée par chef d'équipe, en attente de validation
    VALIDEE,       // Offre validée par responsable RH
    REJETEE,       // Offre rejetée par responsable RH
    PUBLIEE,       // Offre active et visible pour les candidats
    EXPIREE,       // Offre dont la date d'expiration est passée
    FERMEE         // Offre fermée manuellement
}