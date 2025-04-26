package com.recrutement.app.controller;

import com.recrutement.app.dto.CommentaireCandidatPublicationCandidatRequest;
import com.recrutement.app.dto.CommentaireCandidatPublicationEntrepriseRequest;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.service.CommentaireCandidatService;
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
@RequestMapping("/api/commentaires-candidat")
@Tag(name = "Commentaires Candidat", description = "API pour gérer les commentaires des candidats")
public class CommentaireCandidatController {

    private final CommentaireCandidatService commentaireCandidatService;

    @Autowired
    public CommentaireCandidatController(CommentaireCandidatService commentaireCandidatService) {
        this.commentaireCandidatService = commentaireCandidatService;
    }

    @Operation(summary = "Créer un commentaire sur une publication entreprise", description = "Crée un nouveau commentaire pour une publication entreprise")
    @ApiResponse(responseCode = "201", description = "Commentaire créé avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    @PostMapping("/publication-entreprise")
    public ResponseEntity<?> creerCommentairePourPublicationEntreprise(
            @RequestBody CommentaireCandidatPublicationEntrepriseRequest request) {
        try {
            CommentaireCandidat nouveauCommentaire = commentaireCandidatService.creerCommentairePublicationEntreprise(request);
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
            @RequestBody CommentaireCandidatPublicationCandidatRequest request) {
        try {
            CommentaireCandidat nouveauCommentaire = commentaireCandidatService.creerCommentairePublicationCandidat(request);
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
            List<CommentaireCandidat> commentaires = commentaireCandidatService.getCommentairesByPublicationEntrepriseId(publicationId);
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
            List<CommentaireCandidat> commentaires = commentaireCandidatService.getCommentairesByPublicationCandidatId(publicationId);
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
            List<CommentaireCandidat> commentairesNonLus = commentaireCandidatService.getCommentairesNonLusByPublicationEntrepriseId(publicationId);
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
            List<CommentaireCandidat> commentairesNonLus = commentaireCandidatService.getCommentairesNonLusByPublicationCandidatId(publicationId);
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
            CommentaireCandidat commentaire = commentaireCandidatService.marquerCommentaireLu(id);
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
            CommentaireCandidat commentaireModifie = commentaireCandidatService.modifierCommentaire(id, nouveauContenu);
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
            commentaireCandidatService.supprimerCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erreur lors de la suppression du commentaire."));
        }
    }
}