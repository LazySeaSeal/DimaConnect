package com.recrutement.app.mapper;

import com.recrutement.app.dto.*;
import com.recrutement.app.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CandidatMapper {
    CandidatMapper INSTANCE = Mappers.getMapper(CandidatMapper.class);

    @Mapping(target = "emailConfirmed", expression = "java(false)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateInscription", ignore = true)
    @Mapping(target = "telephone", ignore = true)
    @Mapping(target = "shortBio", ignore = true)
    @Mapping(target = "parcours", ignore = true)
    @Mapping(target = "formation", ignore = true)
    @Mapping(target = "experienceProfessionnelle", ignore = true)
    @Mapping(target = "licencesEtCertifications", ignore = true)
    @Mapping(target = "confirmationToken", ignore = true)
    @Mapping(target = "cv", ignore = true)
    @Mapping(target = "competences", ignore = true)
    @Mapping(target = "competenceCandidatDetails", ignore = true)
    @Mapping(target = "publications", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "contactsEnvoyes", ignore = true)
    @Mapping(target = "contactsRecus", ignore = true)
    @Mapping(target = "abonnements", ignore = true)
    @Mapping(target = "candidatures", ignore = true)
    @Mapping(target = "commentaires", ignore = true)
    @Mapping(target = "messagesRecus", ignore = true)
    Candidat toEntity(CandidatRegistrationDTO dto);

    @Mapping(target = "competences", source = "competenceCandidatDetails", qualifiedByName = "mapCompetences")
    @Mapping(target = "profileViewers", source = "contactsRecus", qualifiedByName = "mapProfileViewers")
    @Mapping(target = "activities", source = ".", qualifiedByName = "mapActivities")
    CandidatProfileDTO toProfileDTO(Candidat candidat);

    @Named("mapCompetences")
    default List<CompetenceRatingDTO> mapCompetences(Set<CompetenceCandidat> competenceCandidats) {
        return competenceCandidats.stream()
                .map(cc -> {
                    CompetenceRatingDTO dto = new CompetenceRatingDTO();
                    dto.setCompetenceId(cc.getCompetence().getId());
                    dto.setNom(cc.getCompetence().getNom());
                    dto.setCategorie(cc.getCompetence().getCategorie());
                    dto.setNiveau(cc.getNiveau());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Named("mapProfileViewers")
    default Set<ProfileViewerDTO> mapProfileViewers(Set<ContactCandidat> contacts) {
        return contacts.stream()
                .map(c -> {
                    ProfileViewerDTO dto = new ProfileViewerDTO();
                    dto.setEntrepriseId(c.getSender().getId());
                    dto.setDateConnexion(c.getDateConnexion());
                    dto.setStatut(c.getStatut());
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Named("mapActivities")
    default List<ActivityDTO> mapActivities(Candidat candidat) {
        List<ActivityDTO> posts = candidat.getPublications().stream()
                .map(this::convertPublicationToActivityDTO)
                .collect(Collectors.toList());

        List<ActivityDTO> comments = candidat.getCommentaires().stream()
                .map(this::convertCommentToActivityDTO)
                .collect(Collectors.toList());

        posts.addAll(comments);
        return posts.stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                .collect(Collectors.toList());
    }

    default ActivityDTO convertPublicationToActivityDTO(PublicationCandidat publication) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(publication.getId());
        dto.setType("POST");
        dto.setContent(publication.getContenu());
        dto.setDate(publication.getDatePublication());
        dto.setRelatedId(publication.getId());
        dto.setLikes(publication.getNombreLikes());
        return dto;
    }

    default ActivityDTO convertCommentToActivityDTO(CommentaireCandidat comment) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(comment.getId());
        dto.setType("COMMENT");
        dto.setContent(comment.getContenu());
        dto.setDate(comment.getDateCreation());
        dto.setRelatedId(comment.getPublicationEntreprise().getId());
        dto.setIsRead(comment.getEstLu());
        return dto;
    }
}