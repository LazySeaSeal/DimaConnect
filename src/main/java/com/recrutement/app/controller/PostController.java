package com.recrutement.app.controller;

import com.recrutement.app.dto.ActivityDTO;
import com.recrutement.app.dto.PublicationCandidatDTO;
import com.recrutement.app.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Candidate posts management APIs")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/{candidateId}")
    @Operation(summary = "Create a new post")
    public ResponseEntity<ActivityDTO> createPost(@PathVariable Long candidateId, @Valid @RequestBody PublicationCandidatDTO postDTO) {
        return ResponseEntity.ok(postService.createPost(candidateId, postDTO));
    }

    @PutMapping("/{candidateId}/{postId}")
    @Operation(summary = "Update an existing post")
    public ResponseEntity<ActivityDTO> updatePost(@PathVariable Long candidateId, @PathVariable Long postId, @Valid @RequestBody PublicationCandidatDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(candidateId, postId, postDTO));
    }

    @DeleteMapping("/{candidateId}/{postId}")
    @Operation(summary = "Delete a post")
    public ResponseEntity<Void> deletePost(@PathVariable Long candidateId, @PathVariable Long postId) {
        postService.deletePost(candidateId, postId);
        return ResponseEntity.ok().build();
    }
}