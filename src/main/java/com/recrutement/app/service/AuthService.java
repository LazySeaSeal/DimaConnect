package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.dto.CandidatRegistrationDTO;
import com.recrutement.app.dto.EmailConfirmationDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.mapper.CandidatMapper;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.util.UUID;

@Service
public class AuthService {

    private final CandidatRepository candidatRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CandidatMapper candidatMapper;

    @Autowired
    public AuthService(CandidatRepository candidatRepository, PasswordEncoder passwordEncoder, EmailService emailService, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.candidatMapper = candidatMapper;
    }

    @Transactional
    public CandidatProfileDTO registerCandidat(CandidatRegistrationDTO registrationDTO) {
        if (candidatRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Candidat candidat = candidatMapper.toEntity(registrationDTO);
        candidat.setMotDePasse(passwordEncoder.encode(registrationDTO.getMotDePasse()));
        String confirmationToken = UUID.randomUUID().toString();
        candidat.setConfirmationToken(confirmationToken);
        candidat.setEmailConfirmed(false);
        Candidat savedCandidat = candidatRepository.save(candidat);
        emailService.sendConfirmationEmail(savedCandidat.getEmail(), confirmationToken);
        return candidatMapper.toProfileDTO(savedCandidat);
    }

    @Transactional
    public boolean confirmEmail(EmailConfirmationDTO confirmationDTO) {
        Optional<Candidat> optionalCandidat = candidatRepository.findByEmail(confirmationDTO.getEmail());

        if (optionalCandidat.isPresent()) {
            Candidat candidat = optionalCandidat.get();

            if (confirmationDTO.getToken().equals(candidat.getConfirmationToken())) {
                candidat.setEmailConfirmed(true);
                candidat.setConfirmationToken(null);

                candidatRepository.save(candidat);
                return true;
            }
        }

        return false;
    }

    public boolean verifyCandidateEmailToken(String email, String token) {
        Optional<Candidat> candidatOptional = candidatRepository.findByEmailAndConfirmationToken(email, token);


        if (candidatOptional.isPresent()) {
            Candidat candidat = candidatOptional.get();
            candidat.setEmailConfirmed(true);
            candidat.setConfirmationToken(null);

            candidatRepository.save(candidat);
            return true;
        }

        return false;
    }

}
