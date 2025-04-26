package com.recrutement.app.service;

import com.recrutement.app.dto.InscriptionEmployeDTO;
import com.recrutement.app.dto.InvitationDTO;
import com.recrutement.app.exception.EntrepriseNotFoundException;
import com.recrutement.app.exception.InvitationInvalideException;
import com.recrutement.app.exception.ResourceAlreadyExistsException;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.InvitationEmploye;
import com.recrutement.app.repository.EmployeRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.InvitationEmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvitationService {

    private final InvitationEmployeRepository invitationRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final EmployeRepository employeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InvitationService(
            InvitationEmployeRepository invitationRepository,
            EntrepriseRepository entrepriseRepository,
            EmployeRepository employeRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.invitationRepository = invitationRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.employeRepository = employeRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public InvitationEmploye creerInvitation(InvitationDTO invitationDTO) {
        // Vérifier si l'email est déjà utilisé par un employé
        if (employeRepository.existsByEmail(invitationDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Un employé avec cet email existe déjà");
        }

        // Vérifier si une invitation active existe déjà pour cet email
        if (invitationRepository.existsByEmailAndEstUtilisee(invitationDTO.getEmail(), false)) {
            throw new ResourceAlreadyExistsException("Une invitation est déjà en cours pour cet email");
        }

        // Récupérer l'entreprise
        Entreprise entreprise = entrepriseRepository.findById(invitationDTO.getEntrepriseId())
                .orElseThrow(() -> new EntrepriseNotFoundException("Entreprise non trouvée avec l'ID: " + invitationDTO.getEntrepriseId()));

        // Créer l'invitation
        InvitationEmploye invitation = InvitationEmploye.creerInvitation(
                invitationDTO.getEmail(),
                invitationDTO.getRole(),
                entreprise
        );

        // Sauvegarder l'invitation
        invitation = invitationRepository.save(invitation);

        // Envoyer l'email d'invitation
        emailService.envoyerEmailInvitation(
                invitation.getEmail(),
                invitation.getToken(),
                entreprise.getNom()
        );

        return invitation;
    }

    @Transactional
    public Employe finaliserInscription(InscriptionEmployeDTO inscriptionDTO) {
        // Récupérer l'invitation par le token
        InvitationEmploye invitation = invitationRepository.findByToken(inscriptionDTO.getToken())
                .orElseThrow(() -> new InvitationInvalideException("Token d'invitation invalide"));

        // Vérifier si l'invitation est valide
        if (!invitation.estValide()) {
            throw new InvitationInvalideException("L'invitation a expiré ou a déjà été utilisée");
        }

        // Créer l'employé
        Employe employe = new Employe();
        employe.setEmail(invitation.getEmail());
        employe.setRole(invitation.getRole());
        employe.setMotDePasse(passwordEncoder.encode(inscriptionDTO.getMotDePasse()));
        employe.setNom(inscriptionDTO.getNom());
        employe.setPrenom(inscriptionDTO.getPrenom());
        employe.setTelephone(inscriptionDTO.getTelephone());
        employe.setDateInscription(LocalDate.now());
        employe.setEntreprise(invitation.getEntreprise());

        // Marquer l'invitation comme utilisée
        invitation.setEstUtilisee(true);
        invitationRepository.save(invitation);

        // Sauvegarder l'employé
        return employeRepository.save(employe);
    }

    public List<InvitationEmploye> getInvitationsActives(Long entrepriseId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new EntrepriseNotFoundException("Entreprise non trouvée avec l'ID: " + entrepriseId));

        return invitationRepository.findAll().stream()
                .filter(invitation -> invitation.getEntreprise().getId().equals(entrepriseId))
                .filter(invitation -> !invitation.isEstUtilisee())
                .toList();
    }
}