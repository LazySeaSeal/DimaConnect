package com.recrutement.app.service;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.repository.CommentaireCandidatRepository;
import com.recrutement.app.repository.PublicationCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final PublicationCandidatRepository publicationCandidatRepository;
    private final CommentaireCandidatRepository commentaireCandidatRepository;

    @Autowired
    public ActivityService(PublicationCandidatRepository publicationCandidatRepository, CommentaireCandidatRepository commentaireCandidatRepository) {
        this.publicationCandidatRepository = publicationCandidatRepository;
        this.commentaireCandidatRepository = commentaireCandidatRepository;
    }

    public List<ActivityDTO> getCandidateActivities(Long candidatId) {
        List<ActivityDTO> posts = publicationCandidatRepository.findByCandidatId(candidatId).stream()
                .map(this::convertToActivityDTO)
                .collect(Collectors.toList());

        List<ActivityDTO> comments = commentaireCandidatRepository.findByCandidatId(candidatId).stream()
                .map(this::convertToActivityDTO)
                .collect(Collectors.toList());

        posts.addAll(comments);
        return posts.stream()
                .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                .collect(Collectors.toList());
    }

    private ActivityDTO convertToActivityDTO(PublicationCandidat post) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(post.getId());
        dto.setType("POST");
        dto.setContent(post.getContenu());
        dto.setDate(post.getDatePublication());
        dto.setRelatedId(post.getId());
        dto.setLikes(post.getNombreLikes());
        return dto;
    }

    private ActivityDTO convertToActivityDTO(CommentaireCandidat comment) {
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
