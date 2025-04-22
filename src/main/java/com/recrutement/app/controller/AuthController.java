package com.recrutement.app.controller;

import com.recrutement.app.dto.*;
import com.recrutement.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new candidate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidate registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already in use")
    })
    public ResponseEntity<CandidatProfileDTO> register(@Valid @RequestBody CandidatRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(authService.registerCandidat(registrationDTO));
    }



    @GetMapping("/verify-email")
    @Operation(summary = "Verify candidate's email via confirmation token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verification result")
    })
    public ResponseEntity<Boolean> verifyEmail(
            @RequestParam String email,
            @RequestParam String token) {

        EmailConfirmationDTO dto = new EmailConfirmationDTO();
        dto.setEmail(email);
        dto.setToken(token);

        boolean isVerified = authService.confirmEmail(dto);
        return ResponseEntity.ok(isVerified);
    }

}