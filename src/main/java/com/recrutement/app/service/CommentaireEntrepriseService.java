package com.recrutement.app.service;

import com.recrutement.app.dto.CommentaireEntrepriseRequest;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.CommentaireEntreprise;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.repository.CommentaireEntrepriseRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.PublicationCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireEntrepriseService {

    private final CommentaireEntrepriseRepository commentaireEntrepriseRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final PublicationCandidatRepository publicationCandidatRepository;

    @Autowired
    public CommentaireEntrepriseService(
            CommentaireEntrepriseRepository commentaireEntrepriseRepository,
            EntrepriseRepository entrepriseRepository,
            PublicationCandidatRepository publicationCandidatRepository) {
        this.commentaireEntrepriseRepository = commentaireEntrepriseRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.publicationCandidatRepository = publicationCandidatRepository;
    }

    public CommentaireEntreprise creerCommentaire(CommentaireEntrepriseRequest request) {
        // Récupérer l'entreprise
        Entreprise entreprise = entrepriseRepository.findById(request.getEntrepriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + request.getEntrepriseId()));

        // Récupérer la publication
        PublicationCandidat publication = publicationCandidatRepository.findById(request.getPublicationCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + request.getPublicationCandidatId()));

        // Créer le commentaire
        CommentaireEntreprise commentaire = new CommentaireEntreprise();
        commentaire.setEntreprise(entreprise);
        commentaire.setPublicationCandidat(publication);
        commentaire.setContenu(request.getContenu());

        return commentaireEntrepriseRepository.save(commentaire);
    }

    public List<CommentaireEntreprise> getCommentairesByPublicationId(Long publicationId) {
        PublicationCandidat publication = publicationCandidatRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + publicationId));
        return commentaireEntrepriseRepository.findByPublicationCandidat(publication);
    }

    public List<CommentaireEntreprise> getCommentairesNonLusByPublicationId(Long publicationId) {
        return commentaireEntrepriseRepository.findByPublicationCandidatIdAndEstLu(publicationId, false);
    }

    public CommentaireEntreprise marquerCommentaireLu(Long commentaireId) {
        CommentaireEntreprise commentaire = commentaireEntrepriseRepository.findById(commentaireId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + commentaireId));
        commentaire.setEstLu(true);
        return commentaireEntrepriseRepository.save(commentaire);
    }

    public CommentaireEntreprise modifierCommentaire(Long id, String nouveauContenu) {
        CommentaireEntreprise commentaire = commentaireEntrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + id));
        commentaire.setContenu(nouveauContenu);
        return commentaireEntrepriseRepository.save(commentaire);
    }

    public void supprimerCommentaire(Long id) {
        if (!commentaireEntrepriseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commentaire non trouvé avec l'ID: " + id);
        }
        commentaireEntrepriseRepository.deleteById(id);
    }
}