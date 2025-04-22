package com.recrutement.app.service;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.dto.CommentaireCandidatDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.CommentaireCandidatRepository;
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CommentService {

    private final CandidatRepository candidatRepository;
    private final CommentaireCandidatRepository commentaireCandidatRepository;
    private final PublicationEntrepriseRepository publicationEntrepriseRepository;

    @Autowired
    public CommentService(CandidatRepository candidatRepository, CommentaireCandidatRepository commentaireCandidatRepository, PublicationEntrepriseRepository publicationEntrepriseRepository) {
        this.candidatRepository = candidatRepository;
        this.commentaireCandidatRepository = commentaireCandidatRepository;
        this.publicationEntrepriseRepository = publicationEntrepriseRepository;
    }

    @Transactional
    public ActivityDTO createComment(Long candidatId, CommentaireCandidatDTO commentDTO) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        PublicationEntreprise publication = publicationEntrepriseRepository.findById(commentDTO.getPublicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found"));

        CommentaireCandidat comment = new CommentaireCandidat();
        comment.setCandidat(candidat);
        comment.setPublicationEntreprise(publication);
        comment.setContenu(commentDTO.getContenu());
        comment.setDateCreation(LocalDate.now());
        comment.setEstLu(false);

        CommentaireCandidat savedComment = commentaireCandidatRepository.save(comment);
        return convertToActivityDTO(savedComment);
    }

    @Transactional
    public ActivityDTO updateComment(Long candidatId, Long commentId, CommentaireCandidatDTO commentDTO) {
        CommentaireCandidat comment = commentaireCandidatRepository.findById(commentId)
                .filter(c -> c.getCandidat().getId().equals(candidatId))
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this candidate"));

        comment.setContenu(commentDTO.getContenu());
        comment.setEstLu(commentDTO.getEstLu());

        CommentaireCandidat updatedComment = commentaireCandidatRepository.save(comment);
        return convertToActivityDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long candidatId, Long commentId) {
        CommentaireCandidat comment = commentaireCandidatRepository.findById(commentId)
                .filter(c -> c.getCandidat().getId().equals(candidatId))
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found for this candidate"));
        commentaireCandidatRepository.delete(comment);
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
