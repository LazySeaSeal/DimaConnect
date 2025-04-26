package com.recrutement.app.controller;

import com.recrutement.app.dto.CommentaireEntreprisePublicationCandidatRequest;
import com.recrutement.app.dto.CommentaireEntreprisePublicationEntrepriseRequest;
import com.recrutement.app.model.CommentaireEntreprise;
import com.recrutement.app.service.CommentaireEntrepriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entreprise/commentaires-entreprise")
@Tag(name = "Commentaires Entreprise", description = "API pour gérer les commentaires des entreprises")
public class CommentaireEntrepriseController {

    private final CommentaireEntrepriseService commentaireEntrepriseService;

    @Autowired
    public CommentaireEntrepriseController(CommentaireEntrepriseService commentaireEntrepriseService) {
        this.commentaireEntrepriseService = commentaireEntrepriseService;
    }

    @Operation(summary = "Créer un commentaire sur une publication entreprise", description = "Crée un nouveau commentaire pour une publication entreprise")
    @ApiResponse(responseCode = "201", description = "Commentaire créé avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    @PostMapping("/publication-entreprise")
    public ResponseEntity<?> creerCommentairePourPublicationEntreprise(
            @RequestBody CommentaireEntreprisePublicationEntrepriseRequest request) {
        try {
            CommentaireEntreprise nouveauCommentaire = commentaireEntrepriseService.creerCommentairePublicationEntreprise(request);
            return new ResponseEntity<>(nouveauCommentaire, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur interne du serveur."));
        }
    }

    @Operation(summary = "Créer un commentaire sur une publication candidat", description = "Crée un nouveau commentaire pour une publication candidat")
    @ApiResponse(responseCode = "201", description = "Commentaire créé avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    @PostMapping("/publication-candidat")
    public ResponseEntity<?> creerCommentairePourPublicationCandidat(
            @RequestBody CommentaireEntreprisePublicationCandidatRequest request) {
        try {
            CommentaireEntreprise nouveauCommentaire = commentaireEntrepriseService.creerCommentairePublicationCandidat(request);
            return new ResponseEntity<>(nouveauCommentaire, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur interne du serveur."));
        }
    }

    @Operation(summary = "Récupérer les commentaires par publication entreprise", description = "Récupère tous les commentaires pour une publication entreprise donnée")
    @GetMapping("/publication-entreprise/{publicationId}")
    public ResponseEntity<?> getCommentairesByPublicationEntreprise(
            @Parameter(description = "ID de la publication entreprise") @PathVariable Long publicationId) {
        try {
            List<CommentaireEntreprise> commentaires = commentaireEntrepriseService.getCommentairesByPublicationEntrepriseId(publicationId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la récupération des commentaires."));
        }
    }

    @Operation(summary = "Récupérer les commentaires par publication candidat", description = "Récupère tous les commentaires pour une publication candidat donnée")
    @GetMapping("/publication-candidat/{publicationId}")
    public ResponseEntity<?> getCommentairesByPublicationCandidat(
            @Parameter(description = "ID de la publication candidat") @PathVariable Long publicationId) {
        try {
            List<CommentaireEntreprise> commentaires = commentaireEntrepriseService.getCommentairesByPublicationCandidatId(publicationId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la récupération des commentaires."));
        }
    }

    @Operation(summary = "Récupérer les commentaires non lus pour une publication entreprise", description = "Récupère les commentaires non lus pour une publication entreprise donnée")
    @GetMapping("/publication-entreprise/{publicationId}/non-lu")
    public ResponseEntity<?> getCommentairesNonLusByPublicationEntreprise(
            @Parameter(description = "ID de la publication entreprise") @PathVariable Long publicationId) {
        try {
            List<CommentaireEntreprise> commentairesNonLus = commentaireEntrepriseService.getCommentairesNonLusByPublicationEntrepriseId(publicationId);
            return ResponseEntity.ok(commentairesNonLus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la récupération des commentaires non lus."));
        }
    }

    @Operation(summary = "Récupérer les commentaires non lus pour une publication candidat", description = "Récupère les commentaires non lus pour une publication candidat donnée")
    @GetMapping("/publication-candidat/{publicationId}/non-lu")
    public ResponseEntity<?> getCommentairesNonLusByPublicationCandidat(
            @Parameter(description = "ID de la publication candidat") @PathVariable Long publicationId) {
        try {
            List<CommentaireEntreprise> commentairesNonLus = commentaireEntrepriseService.getCommentairesNonLusByPublicationCandidatId(publicationId);
            return ResponseEntity.ok(commentairesNonLus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la récupération des commentaires non lus."));
        }
    }

    @Operation(summary = "Marquer un commentaire comme lu", description = "Marque un commentaire comme lu")
    @PatchMapping("/{id}/marquer-lu")
    public ResponseEntity<?> marquerCommentaireLu(
            @Parameter(description = "ID du commentaire") @PathVariable Long id) {
        try {
            CommentaireEntreprise commentaire = commentaireEntrepriseService.marquerCommentaireLu(id);
            return ResponseEntity.ok(commentaire);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la mise à jour du commentaire."));
        }
    }

    @Operation(summary = "Modifier un commentaire", description = "Modifie le contenu d'un commentaire existant")
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierCommentaire(
            @Parameter(description = "ID du commentaire") @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String nouveauContenu = request.get("contenu");
            CommentaireEntreprise commentaireModifie = commentaireEntrepriseService.modifierCommentaire(id, nouveauContenu);
            return ResponseEntity.ok(commentaireModifie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la modification du commentaire."));
        }
    }

    @Operation(summary = "Supprimer un commentaire", description = "Supprime un commentaire existant")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerCommentaire(
            @Parameter(description = "ID du commentaire") @PathVariable Long id) {
        try {
            commentaireEntrepriseService.supprimerCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la suppression du commentaire."));
        }
    }
}
