package com.recrutement.app.controller;

import com.recrutement.app.dto.CompetenceRatingDTO;
import com.recrutement.app.service.SkillsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Candidate skills management APIs")
public class SkillsController {

    private final SkillsService skillsService;

    @Autowired
    public SkillsController(SkillsService skillsService) {
        this.skillsService = skillsService;
    }

    @GetMapping("/{candidateId}")
    @Operation(summary = "Get candidate skills")
    public ResponseEntity<List<CompetenceRatingDTO>> getSkills(@PathVariable Long candidateId) {
        return ResponseEntity.ok(skillsService.getCandidateSkills(candidateId));
    }

    @PostMapping("/{candidateId}")
    @Operation(summary = "Add or update candidate skill")
    public ResponseEntity<Void> addOrUpdateSkill(@PathVariable Long candidateId, @Valid @RequestBody CompetenceRatingDTO skillDTO) {
        skillsService.addOrUpdateSkill(candidateId, skillDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{candidateId}/{skillId}")
    @Operation(summary = "Remove candidate skill")
    public ResponseEntity<Void> removeSkill(@PathVariable Long candidateId, @PathVariable Long skillId) {
        skillsService.removeSkill(candidateId, skillId);
        return ResponseEntity.ok().build();
    }
}