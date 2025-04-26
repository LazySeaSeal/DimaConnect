package com.recrutement.app.controller;

import com.recrutement.app.dto.JwtAuthResponse;
import com.recrutement.app.dto.LoginDto;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.EmployeRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            // Format du username pour Spring Security: "type:email"
            String username = loginDto.getType() + ":" + loginDto.getEmail();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDto.getMotDePasse())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Récupérer les informations de l'utilisateur selon son type
            JwtAuthResponse responseDto;
            if ("employe".equals(loginDto.getType())) {
                Optional<Employe> employe = employeRepository.findByEmail(loginDto.getEmail());
                if (employe.isPresent()) {
                    // Pass userType and email to generateToken
                    String token = tokenProvider.generateToken(
                            authentication,
                            "employe",
                            loginDto.getEmail()
                    );

                    responseDto = new JwtAuthResponse(
                            token,
                            "Bearer",
                            employe.get().getId(),
                            employe.get().getNom() + " " + employe.get().getPrenom(),
                            employe.get().getRole().name()
                    );
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé");
                }
            } else {
                Optional<Entreprise> entreprise = entrepriseRepository.findByEmail(loginDto.getEmail());
                if (entreprise.isPresent()) {
                    // Pass userType and email to generateToken
                    String token = tokenProvider.generateToken(
                            authentication,
                            "entreprise",
                            loginDto.getEmail()
                    );

                    responseDto = new JwtAuthResponse(
                            token,
                            "Bearer",
                            entreprise.get().getId(),
                            entreprise.get().getNom(),
                            "ENTREPRISE"
                    );
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entreprise non trouvée");
                }
            }

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
    }
}