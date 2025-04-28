package com.recrutement.app.service;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.dto.PublicationCandidatDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.PublicationCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PostService {

    private final CandidatRepository candidatRepository;
    private final PublicationCandidatRepository publicationCandidatRepository;

    @Autowired
    public PostService(CandidatRepository candidatRepository, PublicationCandidatRepository publicationCandidatRepository) {
        this.candidatRepository = candidatRepository;
        this.publicationCandidatRepository = publicationCandidatRepository;
    }

    @Transactional
    public ActivityDTO createPost(Long candidatId, PublicationCandidatDTO postDTO) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        PublicationCandidat post = new PublicationCandidat();
        post.setCandidat(candidat);
        post.setContenu(postDTO.getContenu());
        post.setMediaUrl(postDTO.getMediaUrl());
        post.setTypeMedia(postDTO.getTypeMedia());
        post.setDatePublication(LocalDate.now());

        PublicationCandidat savedPost = publicationCandidatRepository.save(post);
        return convertToActivityDTO(savedPost);
    }

    @Transactional
    public ActivityDTO updatePost(Long candidatId, Long postId, PublicationCandidatDTO postDTO) {
        PublicationCandidat post = publicationCandidatRepository.findById(postId)
                .filter(p -> p.getCandidat().getId().equals(candidatId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found for this candidate"));

        post.setContenu(postDTO.getContenu());
        post.setMediaUrl(postDTO.getMediaUrl());
        post.setTypeMedia(postDTO.getTypeMedia());

        PublicationCandidat updatedPost = publicationCandidatRepository.save(post);
        return convertToActivityDTO(updatedPost);
    }

    @Transactional
    public void deletePost(Long candidatId, Long postId) {
        PublicationCandidat post = publicationCandidatRepository.findById(postId)
                .filter(p -> p.getCandidat().getId().equals(candidatId))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found for this candidate"));
        publicationCandidatRepository.delete(post);
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
}
