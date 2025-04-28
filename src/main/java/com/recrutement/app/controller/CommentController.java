package com.recrutement.app.controller;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.dto.CommentaireCandidatDTO;
import com.recrutement.app.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Candidate comments management APIs")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{candidateId}")
    @Operation(summary = "Create a comment on a company post")
    public ResponseEntity<ActivityDTO> createComment(@PathVariable Long candidateId, @Valid @RequestBody CommentaireCandidatDTO commentDTO) {
        return ResponseEntity.ok(commentService.createComment(candidateId, commentDTO));
    }

    @PutMapping("/{candidateId}/{commentId}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<ActivityDTO> updateComment(@PathVariable Long candidateId, @PathVariable Long commentId, @Valid @RequestBody CommentaireCandidatDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(candidateId, commentId, commentDTO));
    }

    @DeleteMapping("/{candidateId}/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long candidateId, @PathVariable Long commentId) {
        commentService.deleteComment(candidateId, commentId);
        return ResponseEntity.ok().build();
    }
}
