package com.recrutement.app.service;

import com.recrutement.app.dto.*;
import com.recrutement.app.model.*;
import com.recrutement.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntrepriseProfilService {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private PublicationEntrepriseRepository publicationEntrepriseRepository;

    @Autowired
    private CommentaireCandidatRepository commentaireCandidatRepository;

    public Optional<EntrepriseProfilDTO> getProfilEntreprise(Long entrepriseId) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);

        if (entrepriseOpt.isEmpty()) {
            return Optional.empty();
        }

        Entreprise entreprise = entrepriseOpt.get();

        // Récupérer les événements à venir
        List<Evenement> evenements = evenementRepository.findByEntrepriseIdAndDateFinAfter(
                entrepriseId, LocalDateTime.now());

        // Récupérer les publications (triées par date)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("datePublication").descending());
        List<PublicationEntreprise> publications = publicationEntrepriseRepository
                .findByEntrepriseId(entrepriseId, pageable).getContent();

        // Construire le DTO
        EntrepriseProfilDTO profilDTO = new EntrepriseProfilDTO();
        profilDTO.setId(entreprise.getId());
        profilDTO.setNom(entreprise.getNom());
        profilDTO.setDescription(entreprise.getDescription());
        profilDTO.setSecteurActivite(entreprise.getSecteurActivite());
        profilDTO.setUrlSite(entreprise.getUrlSite());
        profilDTO.setNombreEmployes(entreprise.getNombreEmployes());
        profilDTO.setDateCreation(entreprise.getDateCreation());
        profilDTO.setEstVerifiee(entreprise.getEstVerifiee());
        profilDTO.setEstPremium(entreprise.getEstPremium());

        // Mapper les événements
        List<EvenementDTO> evenementDTOs = evenements.stream()
                .map(this::mapToEvenementDTO)
                .collect(Collectors.toList());
        profilDTO.setEvenements(evenementDTOs);

        // Mapper les publications et leurs commentaires
        List<PublicationDTO> publicationDTOs = publications.stream()
                .map(this::mapToPublicationDTO)
                .collect(Collectors.toList());
        profilDTO.setPublications(publicationDTOs);

        return Optional.of(profilDTO);
    }

    private EvenementDTO mapToEvenementDTO(Evenement evenement) {
        EvenementDTO dto = new EvenementDTO();
        dto.setId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDescription(evenement.getDescription());
        dto.setDateDebut(evenement.getDateDebut());
        dto.setDateFin(evenement.getDateFin());
        dto.setTypeEvenement(evenement.getTypeEvenement());
        dto.setLieu(evenement.getLieu());
        dto.setEstVirtuel(evenement.getEstVirtuel());
        dto.setLienVirtuel(evenement.getLienVirtuel());
        return dto;
    }

    private PublicationDTO mapToPublicationDTO(PublicationEntreprise publication) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(publication.getId());
        dto.setContenu(publication.getContenu());
        dto.setMediaUrl(publication.getMediaUrl());
        dto.setTypeMedia(publication.getTypeMedia() != null ? publication.getTypeMedia().toString() : null);
        dto.setDatePublication(publication.getDatePublication());
        dto.setNombreLikes(publication.getNombreLikes());

        // Récupérer et mapper les commentaires de cette publication
        List<CommentaireCandidat> commentaires = commentaireCandidatRepository
                .findByPublicationEntrepriseId(publication.getId());

        List<CommentaireDTO> commentaireDTOs = commentaires.stream()
                .map(c -> {
                    CommentaireDTO commentDTO = new CommentaireDTO();
                    commentDTO.setId(c.getId());
                    commentDTO.setContenu(c.getContenu());
                    commentDTO.setDateCreation(c.getDateCreation());
                    commentDTO.setAuteurNom(c.getCandidat().getNom() + " " + c.getCandidat().getPrenom());
                    commentDTO.setAuteurType("CANDIDAT");
                    return commentDTO;
                })
                .collect(Collectors.toList());

        dto.setCommentaires(commentaireDTOs);
        return dto;
    }
}