package com.recrutement.app.controller;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Candidate profile management APIs")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candidate profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")
    })
    public ResponseEntity<CandidatProfileDTO> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getCandidatProfile(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update candidate profile")
    public ResponseEntity<CandidatProfileDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody CandidatProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.updateProfile(id, profileDTO));
    }
}
