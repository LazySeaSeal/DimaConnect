package com.recrutement.app.controller;

import com.recrutement.app.dto.CommentaireCandidatRequest;
import com.recrutement.app.model.CommentaireCandidat;
import com.recrutement.app.service.CommentaireCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commentaires-candidat")
public class CommentaireCandidatController {

    private final CommentaireCandidatService commentaireCandidatService;

    @Autowired
    public CommentaireCandidatController(CommentaireCandidatService commentaireCandidatService) {
        this.commentaireCandidatService = commentaireCandidatService;
    }

    @PostMapping
    public ResponseEntity<CommentaireCandidat> creerCommentaire(@RequestBody CommentaireCandidatRequest request) {
        CommentaireCandidat nouveauCommentaire = commentaireCandidatService.creerCommentaire(request);
        return new ResponseEntity<>(nouveauCommentaire, HttpStatus.CREATED);
    }

    @GetMapping("/publication/{publicationId}")
    public ResponseEntity<List<CommentaireCandidat>> getCommentairesByPublication(@PathVariable Long publicationId) {
        List<CommentaireCandidat> commentaires = commentaireCandidatService.getCommentairesByPublicationId(publicationId);
        return ResponseEntity.ok(commentaires);
    }

    @GetMapping("/publication/{publicationId}/non-lu")
    public ResponseEntity<List<CommentaireCandidat>> getCommentairesNonLus(@PathVariable Long publicationId) {
        List<CommentaireCandidat> commentairesNonLus = commentaireCandidatService.getCommentairesNonLusByPublicationId(publicationId);
        return ResponseEntity.ok(commentairesNonLus);
    }

    @PatchMapping("/{id}/marquer-lu")
    public ResponseEntity<CommentaireCandidat> marquerCommentaireLu(@PathVariable Long id) {
        CommentaireCandidat commentaire = commentaireCandidatService.marquerCommentaireLu(id);
        return ResponseEntity.ok(commentaire);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentaireCandidat> modifierCommentaire(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String nouveauContenu = request.get("contenu");
        CommentaireCandidat commentaireModifie = commentaireCandidatService.modifierCommentaire(id, nouveauContenu);
        return ResponseEntity.ok(commentaireModifie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCommentaire(@PathVariable Long id) {
        commentaireCandidatService.supprimerCommentaire(id);
        return ResponseEntity.noContent().build();
    }
}