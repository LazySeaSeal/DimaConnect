package com.recrutement.app.service;

import com.recrutement.app.dto.CommentaireCandidatRequest;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.repository.CommentaireCandidatRepository;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireCandidatService {

    private final CommentaireCandidatRepository commentaireCandidatRepository;
    private final CandidatRepository candidatRepository;
    private final PublicationEntrepriseRepository publicationEntrepriseRepository;

    @Autowired
    public CommentaireCandidatService(
            CommentaireCandidatRepository commentaireCandidatRepository,
            CandidatRepository candidatRepository,
            PublicationEntrepriseRepository publicationEntrepriseRepository) {
        this.commentaireCandidatRepository = commentaireCandidatRepository;
        this.candidatRepository = candidatRepository;
        this.publicationEntrepriseRepository = publicationEntrepriseRepository;
    }

    public CommentaireCandidat creerCommentaire(CommentaireCandidatRequest request) {
        // Récupérer le candidat
        Candidat candidat = candidatRepository.findById(request.getCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'ID: " + request.getCandidatId()));

        // Récupérer la publication
        PublicationEntreprise publication = publicationEntrepriseRepository.findById(request.getPublicationEntrepriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + request.getPublicationEntrepriseId()));

        // Créer le commentaire
        CommentaireCandidat commentaire = new CommentaireCandidat();
        commentaire.setCandidat(candidat);
        commentaire.setPublicationEntreprise(publication);
        commentaire.setContenu(request.getContenu());

        return commentaireCandidatRepository.save(commentaire);
    }

    public List<CommentaireCandidat> getCommentairesByPublicationId(Long publicationId) {
        PublicationEntreprise publication = publicationEntrepriseRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + publicationId));
        return commentaireCandidatRepository.findByPublicationEntreprise(publication);
    }

    public List<CommentaireCandidat> getCommentairesNonLusByPublicationId(Long publicationId) {
        return commentaireCandidatRepository.findByPublicationEntrepriseIdAndEstLu(publicationId, false);
    }

    public CommentaireCandidat marquerCommentaireLu(Long commentaireId) {
        CommentaireCandidat commentaire = commentaireCandidatRepository.findById(commentaireId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + commentaireId));
        commentaire.setEstLu(true);
        return commentaireCandidatRepository.save(commentaire);
    }

    public CommentaireCandidat modifierCommentaire(Long id, String nouveauContenu) {
        CommentaireCandidat commentaire = commentaireCandidatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + id));
        commentaire.setContenu(nouveauContenu);
        return commentaireCandidatRepository.save(commentaire);
    }

    public void supprimerCommentaire(Long id) {
        if (!commentaireCandidatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + id);
        }
        commentaireCandidatRepository.deleteById(id);
    }
}