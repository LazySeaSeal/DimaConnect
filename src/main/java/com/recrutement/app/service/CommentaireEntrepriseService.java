package com.recrutement.app.service;

import com.recrutement.app.dto.CommentaireEntreprisePublicationCandidatRequest;
import com.recrutement.app.dto.CommentaireEntreprisePublicationEntrepriseRequest;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.model.CommentaireEntreprise;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.PublicationCandidat;
import com.recrutement.app.model.PublicationEntreprise;
import com.recrutement.app.repository.CommentaireEntrepriseRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.PublicationCandidatRepository;
import com.recrutement.app.repository.PublicationEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireEntrepriseService {

    private final CommentaireEntrepriseRepository commentaireEntrepriseRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final PublicationCandidatRepository publicationCandidatRepository;
    private final PublicationEntrepriseRepository publicationEntrepriseRepository;

    @Autowired
    public CommentaireEntrepriseService(
            CommentaireEntrepriseRepository commentaireEntrepriseRepository,
            EntrepriseRepository entrepriseRepository,
            PublicationCandidatRepository publicationCandidatRepository,
            PublicationEntrepriseRepository publicationEntrepriseRepository) {
        this.commentaireEntrepriseRepository = commentaireEntrepriseRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.publicationCandidatRepository = publicationCandidatRepository;
        this.publicationEntrepriseRepository = publicationEntrepriseRepository;
    }

    public CommentaireEntreprise creerCommentairePublicationCandidat(CommentaireEntreprisePublicationCandidatRequest request) {
        // Récupérer l'entreprise
        Entreprise entreprise = entrepriseRepository.findById(request.getEntrepriseId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + request.getEntrepriseId()));

        // Récupérer la publication du candidat
        PublicationCandidat publicationCandidat = publicationCandidatRepository.findById(request.getPublicationCandidatId())
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + request.getPublicationCandidatId()));

        // Créer le commentaire
        CommentaireEntreprise commentaire = new CommentaireEntreprise();
        commentaire.setEntreprise(entreprise);
        commentaire.setPublicationCandidat(publicationCandidat);
        commentaire.setContenu(request.getContenu());

        return commentaireEntrepriseRepository.save(commentaire);
    }

  public CommentaireEntreprise creerCommentairePublicationEntreprise(CommentaireEntreprisePublicationEntrepriseRequest request) {
    // Récupérer l'entreprise
    Entreprise entreprise = entrepriseRepository.findById(request.getEntrepriseId())
            .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + request.getEntrepriseId()));

    // Récupérer la publication de l'entreprise
    PublicationEntreprise publicationEntreprise = publicationEntrepriseRepository.findById(request.getPublicationEntrepriseId())
            .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + request.getPublicationEntrepriseId()));

    // Créer le commentaire
    CommentaireEntreprise commentaire = new CommentaireEntreprise();
    commentaire.setEntreprise(entreprise);
    commentaire.setPublicationEntreprise(publicationEntreprise);
    commentaire.setPublicationCandidat(null); // Explicitly set to null
    commentaire.setContenu(request.getContenu());

    return commentaireEntrepriseRepository.save(commentaire);
}

    public List<CommentaireEntreprise> getCommentairesByPublicationCandidatId(Long publicationCandidatId) {
        PublicationCandidat publicationCandidat = publicationCandidatRepository.findById(publicationCandidatId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication candidat non trouvée avec l'ID: " + publicationCandidatId));
        return commentaireEntrepriseRepository.findByPublicationCandidat(publicationCandidat);
    }

    public List<CommentaireEntreprise> getCommentairesByPublicationEntrepriseId(Long publicationEntrepriseId) {
        PublicationEntreprise publicationEntreprise = publicationEntrepriseRepository.findById(publicationEntrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication entreprise non trouvée avec l'ID: " + publicationEntrepriseId));
        return commentaireEntrepriseRepository.findByPublicationEntreprise(publicationEntreprise);
    }

    public List<CommentaireEntreprise> getCommentairesByEntrepriseId(Long entrepriseId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'ID: " + entrepriseId));
        return commentaireEntrepriseRepository.findByEntreprise(entreprise);
    }

    public List<CommentaireEntreprise> getCommentairesNonLusByPublicationCandidatId(Long publicationCandidatId) {
        return commentaireEntrepriseRepository.findByPublicationCandidatIdAndEstLu(publicationCandidatId, false);
    }

    public List<CommentaireEntreprise> getCommentairesNonLusByPublicationEntrepriseId(Long publicationEntrepriseId) {
        return commentaireEntrepriseRepository.findByPublicationEntrepriseIdAndEstLu(publicationEntrepriseId, false);
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