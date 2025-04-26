package com.recrutement.app.service;

import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.exception.ResourceAlreadyExistsException;
import com.recrutement.app.dto.EntrepriseInscriptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public EntrepriseService(EntrepriseRepository entrepriseRepository,
                             PasswordEncoder passwordEncoder,
                             EmailService emailService) {
        this.entrepriseRepository = entrepriseRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Entreprise inscrireEntreprise(EntrepriseInscriptionDto inscriptionDto) {
        // Vérifier si l'email existe déjà
        if (entrepriseRepository.existsByEmail(inscriptionDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Une entreprise avec cet email existe déjà");
        }

        // Vérifier si le SIRET existe déjà s'il est fourni
        if (inscriptionDto.getSiret() != null && !inscriptionDto.getSiret().isEmpty() &&
                entrepriseRepository.existsBySiret(inscriptionDto.getSiret())) {
            throw new ResourceAlreadyExistsException("Une entreprise avec ce SIRET existe déjà");
        }

        // Créer une nouvelle entreprise
        Entreprise entreprise = new Entreprise();
        entreprise.setNom(inscriptionDto.getNom());
        entreprise.setEmail(inscriptionDto.getEmail());
        entreprise.setMotDePasse(passwordEncoder.encode(inscriptionDto.getMotDePasse()));
        entreprise.setSiret(inscriptionDto.getSiret());
        entreprise.setUrlSite(inscriptionDto.getUrlSite());
        entreprise.setDescription(inscriptionDto.getDescription());
        entreprise.setNombreEmployes(inscriptionDto.getNombreEmployes());
        entreprise.setSecteurActivite(inscriptionDto.getSecteurActivite());
        entreprise.setDateCreation(LocalDate.now());
        entreprise.setEstVerifiee(false);
        entreprise.setEstPremium(false);

        // Enregistrer l'entreprise
        Entreprise entrepriseSauvegardee = entrepriseRepository.save(entreprise);

        // Envoyer un email de confirmation
        envoyerEmailConfirmation(entrepriseSauvegardee);

        return entrepriseSauvegardee;
    }

    private void envoyerEmailConfirmation(Entreprise entreprise) {
        String sujet = "Confirmation de votre inscription sur RecruitMe";
        String message = "Bonjour " + entreprise.getNom() + ",\n\n" +
                "Nous vous remercions de votre inscription sur notre plateforme de recrutement.\n" +
                "Votre compte a été créé avec succès.\n\n" +
                "Cordialement,\n" +
                "L'équipe RecruitMe";

        emailService.envoyerEmail(entreprise.getEmail(), sujet, message);
    }

    public List<Entreprise> obtenirToutesEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Optional<Entreprise> obtenirEntrepriseParId(Long id) {
        return entrepriseRepository.findById(id);
    }

    public Optional<Entreprise> obtenirEntrepriseParEmail(String email) {
        return entrepriseRepository.findByEmail(email);
    }

    @Transactional
    public Entreprise mettreAJourEntreprise(Long id, Entreprise entrepriseDetails) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id : " + id));

        entreprise.setNom(entrepriseDetails.getNom());
        entreprise.setDescription(entrepriseDetails.getDescription());
        entreprise.setUrlSite(entrepriseDetails.getUrlSite());
        entreprise.setNombreEmployes(entrepriseDetails.getNombreEmployes());
        entreprise.setSecteurActivite(entrepriseDetails.getSecteurActivite());

        return entrepriseRepository.save(entreprise);
    }

    @Transactional
    public void supprimerEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }

    @Transactional
    public void verifierEntreprise(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id : " + id));
        entreprise.setEstVerifiee(true);
        entrepriseRepository.save(entreprise);
    }


}