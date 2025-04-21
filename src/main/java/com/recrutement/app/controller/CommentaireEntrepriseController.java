package com.recrutement.app.controller;

import com.recrutement.app.dto.CommentaireEntrepriseRequest;
import com.recrutement.app.model.CommentaireEntreprise;
import com.recrutement.app.service.CommentaireEntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/commentaires-entreprise")
public class CommentaireEntrepriseController {

    private final CommentaireEntrepriseService commentaireEntrepriseService;

    @Autowired
    public CommentaireEntrepriseController(CommentaireEntrepriseService commentaireEntrepriseService) {
        this.commentaireEntrepriseService = commentaireEntrepriseService;
    }

    @PostMapping
    public ResponseEntity<CommentaireEntreprise> creerCommentaire(@RequestBody CommentaireEntrepriseRequest request) {
        CommentaireEntreprise nouveauCommentaire = commentaireEntrepriseService.creerCommentaire(request);
        return new ResponseEntity<>(nouveauCommentaire, HttpStatus.CREATED);
    }

    @GetMapping("/publication/{publicationId}")
    public ResponseEntity<List<CommentaireEntreprise>> getCommentairesByPublication(@PathVariable Long publicationId) {
        List<CommentaireEntreprise> commentaires = commentaireEntrepriseService.getCommentairesByPublicationId(publicationId);
        return ResponseEntity.ok(commentaires);
    }

    @GetMapping("/publication/{publicationId}/non-lu")
    public ResponseEntity<List<CommentaireEntreprise>> getCommentairesNonLus(@PathVariable Long publicationId) {
        List<CommentaireEntreprise> commentairesNonLus = commentaireEntrepriseService.getCommentairesNonLusByPublicationId(publicationId);
        return ResponseEntity.ok(commentairesNonLus);
    }

    @PatchMapping("/{id}/marquer-lu")
    public ResponseEntity<CommentaireEntreprise> marquerCommentaireLu(@PathVariable Long id) {
        CommentaireEntreprise commentaire = commentaireEntrepriseService.marquerCommentaireLu(id);
        return ResponseEntity.ok(commentaire);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentaireEntreprise> modifierCommentaire(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String nouveauContenu = request.get("contenu");
        CommentaireEntreprise commentaireModifie = commentaireEntrepriseService.modifierCommentaire(id, nouveauContenu);
        return ResponseEntity.ok(commentaireModifie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommentaire(@PathVariable Long id) {
        commentaireEntrepriseService.supprimerCommentaire(id);
        return ResponseEntity.noContent().build();
    }
}