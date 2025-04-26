package com.recrutement.app.controller;

import com.recrutement.app.config.security.AuthRequest;
import com.recrutement.app.config.security.AuthResponse;
import com.recrutement.app.config.security.JwtUtils;
import com.recrutement.app.config.security.Role;
import com.recrutement.app.config.security.UserDetailsImpl;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.EntrepriseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/candidat/login")
    @Operation(summary = "Authentification d'un candidat", 
               description = "Permet à un candidat de s'authentifier et d'obtenir un token JWT",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Authentification réussie", 
                                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Authentification échouée")
               })
    public ResponseEntity<?> authenticateCandidat(@Valid @RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        if (!roles.contains(Role.ROLE_CANDIDAT)) {
            return ResponseEntity.badRequest().body("Erreur: Ce n'est pas un compte candidat");
        }
        
        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(), Role.ROLE_CANDIDAT));
    }

    @PostMapping("/entreprise/login")
    @Operation(summary = "Authentification d'une entreprise", 
               description = "Permet à une entreprise de s'authentifier et d'obtenir un token JWT",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Authentification réussie", 
                                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Authentification échouée")
               })
    public ResponseEntity<?> authenticateEntreprise(@Valid @RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        if (!roles.contains(Role.ROLE_ENTREPRISE)) {
            return ResponseEntity.badRequest().body("Erreur: Ce n'est pas un compte entreprise");
        }
        
        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(), Role.ROLE_ENTREPRISE));
    }

    @PostMapping("/candidat/register")
    @Operation(summary = "Inscription d'un candidat", 
               description = "Permet à un candidat de s'inscrire",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Inscription réussie"),
                   @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
               })
    public ResponseEntity<?> registerCandidat(@Valid @RequestBody Candidat candidat) {
        if (candidatRepository.existsByEmail(candidat.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur: Email déjà utilisé!");
        }

        // Encoder le mot de passe
        candidat.setMotDePasse(encoder.encode(candidat.getMotDePasse()));
        
        // Sauvegarder le candidat dans la base de données
        candidatRepository.save(candidat);

        return ResponseEntity.ok("Candidat enregistré avec succès!");
    }

    @PostMapping("/entreprise/register")
    @Operation(summary = "Inscription d'une entreprise", 
               description = "Permet à une entreprise de s'inscrire",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Inscription réussie"),
                   @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
               })
    public ResponseEntity<?> registerEntreprise(@Valid @RequestBody Entreprise entreprise) {
        if (entrepriseRepository.existsByEmail(entreprise.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur: Email déjà utilisé!");
        }

        // Encoder le mot de passe
        entreprise.setMotDePasse(encoder.encode(entreprise.getMotDePasse()));
        
        // Sauvegarder l'entreprise dans la base de données
        entrepriseRepository.save(entreprise);

        return ResponseEntity.ok("Entreprise enregistrée avec succès!");
    }
}
