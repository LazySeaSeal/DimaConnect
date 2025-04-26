package com.recrutement.app.service;

import com.recrutement.app.dto.CommentaireCandidatPublicationCandidatRequest;
import com.recrutement.app.dto.CommentaireCandidatPublicationEntrepriseRequest;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.CommentaireCandidatRepository;
import com.recrutement.app.repository.PublicationCandidatRepository;
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireCandidatService {

    private final CommentaireCandidatRepository commentaireCandidatRepository;
    private final CandidatRepository candidatRepository;
    private final PublicationCandidatRepository publicationCandidatRepository;
    private final PublicationEntrepriseRepository publicationEntrepriseRepository;

    @Autowired
    public CommentaireCandidatService(
            CommentaireCandidatRepository commentaireCandidatRepository,
            CandidatRepository candidatRepository,
            PublicationCandidatRepository publicationCandidatRepository,
            PublicationEntrepriseRepository publicationEntrepriseRepository) {
        this.commentaireCandidatRepository = commentaireCandidatRepository;
        this.candidatRepository = candidatRepository;
        this.publicationCandidatRepository = publicationCandidatRepository;
        this.publicationEntrepriseRepository = publicationEntrepriseRepository;
    }

    public CommentaireCandidat creerCommentairePublicationEntreprise(CommentaireCandidatPublicationEntrepriseRequest request) {
        // Récupérer le candidat
        Candidat candidat = candidatRepository.findById(request.getCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'ID: " + request.getCandidatId()));

        // Récupérer la publication de l'entreprise
        PublicationEntreprise publicationEntreprise = publicationEntrepriseRepository.findById(request.getPublicationEntrepriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + request.getPublicationEntrepriseId()));

        // Créer le commentaire
        CommentaireCandidat commentaire = new CommentaireCandidat();
        commentaire.setCandidat(candidat);
        commentaire.setPublicationEntreprise(publicationEntreprise);
        commentaire.setPublicationCandidat(null); // Explicitly set to null
        commentaire.setContenu(request.getContenu());

        return commentaireCandidatRepository.save(commentaire);
    }

    public CommentaireCandidat creerCommentairePublicationCandidat(CommentaireCandidatPublicationCandidatRequest request) {
        // Récupérer le candidat
        Candidat candidat = candidatRepository.findById(request.getCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'ID: " + request.getCandidatId()));

        // Récupérer la publication du candidat
        PublicationCandidat publicationCandidat = publicationCandidatRepository.findById(request.getPublicationCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + request.getPublicationCandidatId()));

        // Créer le commentaire
        CommentaireCandidat commentaire = new CommentaireCandidat();
        commentaire.setCandidat(candidat);
        commentaire.setPublicationCandidat(publicationCandidat);
        commentaire.setPublicationEntreprise(null); // Explicitly set to null
        commentaire.setContenu(request.getContenu());

        return commentaireCandidatRepository.save(commentaire);
    }

    public List<CommentaireCandidat> getCommentairesByPublicationEntrepriseId(Long publicationEntrepriseId) {
        PublicationEntreprise publicationEntreprise = publicationEntrepriseRepository.findById(publicationEntrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + publicationEntrepriseId));
        return commentaireCandidatRepository.findByPublicationEntreprise(publicationEntreprise);
    }

    public List<CommentaireCandidat> getCommentairesByPublicationCandidatId(Long publicationCandidatId) {
        PublicationCandidat publicationCandidat = publicationCandidatRepository.findById(publicationCandidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + publicationCandidatId));
        return commentaireCandidatRepository.findByPublicationCandidat(publicationCandidat);
    }

    public List<CommentaireCandidat> getCommentairesByCandidatId(Long candidatId) {
        Candidat candidat = candidatRepository.findById(candidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'ID: " + candidatId));
        return commentaireCandidatRepository.findByCandidat(candidat);
    }

    public List<CommentaireCandidat> getCommentairesNonLusByPublicationEntrepriseId(Long publicationEntrepriseId) {
        return commentaireCandidatRepository.findByPublicationEntrepriseIdAndEstLu(publicationEntrepriseId, false);
    }

    public List<CommentaireCandidat> getCommentairesNonLusByPublicationCandidatId(Long publicationCandidatId) {
        return commentaireCandidatRepository.findByPublicationCandidatIdAndEstLu(publicationCandidatId, false);
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