package com.recrutement.app.service;

import com.recrutement.app.dto.EntrepriseProfilDTO;
import com.recrutement.app.dto.EntrepriseUpdateDTO;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.enums.RoleEmploye;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.EmployeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AdminEntrepriseProfilService {

    private static final Logger logger = LoggerFactory.getLogger(AdminEntrepriseProfilService.class);

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EntrepriseProfilService entrepriseProfilService;

    /**
     * Met à jour le profil d'une entreprise (accessible uniquement par un admin)
     */
    @Transactional
    public Optional<EntrepriseProfilDTO> updateEntrepriseProfil(Long entrepriseId, EntrepriseUpdateDTO updateDTO) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);

        if (entrepriseOpt.isEmpty()) {
            return Optional.empty();
        }

        Entreprise entreprise = entrepriseOpt.get();

        // Mise à jour des champs modifiables
        if (updateDTO.getNom() != null) {
            entreprise.setNom(updateDTO.getNom());
        }

        if (updateDTO.getDescription() != null) {
            entreprise.setDescription(updateDTO.getDescription());
        }

        if (updateDTO.getSecteurActivite() != null) {
            entreprise.setSecteurActivite(updateDTO.getSecteurActivite());
        }

        if (updateDTO.getUrlSite() != null) {
            entreprise.setUrlSite(updateDTO.getUrlSite());
        }

        if (updateDTO.getNombreEmployes() != null) {
            entreprise.setNombreEmployes(updateDTO.getNombreEmployes());
        }

        if (updateDTO.getSiret() != null) {
            entreprise.setSiret(updateDTO.getSiret());
        }

        if (updateDTO.getEstVerifiee() != null) {
            entreprise.setEstVerifiee(updateDTO.getEstVerifiee());
        }

        if (updateDTO.getEstPremium() != null) {
            entreprise.setEstPremium(updateDTO.getEstPremium());

            // Si on active le premium, on définit une date d'expiration par défaut (1 an)
            if (updateDTO.getEstPremium() && (entreprise.getDateExpiration() == null ||
                    entreprise.getDateExpiration().isBefore(LocalDate.now()))) {
                entreprise.setDateExpiration(LocalDate.now().plusYears(1));
            }
        }

        if (updateDTO.getDateExpiration() != null) {
            entreprise.setDateExpiration(updateDTO.getDateExpiration());
        }

        // Enregistrer les modifications
        entrepriseRepository.save(entreprise);

        // Retourner le profil mis à jour
        return entrepriseProfilService.getProfilEntreprise(entrepriseId);
    }

    /**
     * Vérifie une entreprise (certification)
     */
    @Transactional
    public Optional<EntrepriseProfilDTO> verifierEntreprise(Long entrepriseId) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);

        if (entrepriseOpt.isEmpty()) {
            return Optional.empty();
        }

        Entreprise entreprise = entrepriseOpt.get();
        entreprise.setEstVerifiee(true);
        entrepriseRepository.save(entreprise);

        return entrepriseProfilService.getProfilEntreprise(entrepriseId);
    }

    /**
     * Active/désactive le statut premium d'une entreprise
     */
    @Transactional
    public Optional<EntrepriseProfilDTO> togglePremiumStatus(Long entrepriseId, LocalDate dateExpiration) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);

        if (entrepriseOpt.isEmpty()) {
            return Optional.empty();
        }

        Entreprise entreprise = entrepriseOpt.get();

        // Toggle premium status
        boolean newStatus = !entreprise.getEstPremium();
        entreprise.setEstPremium(newStatus);

        // Si premium activé, mettre à jour la date d'expiration
        if (newStatus) {
            entreprise.setDateExpiration(dateExpiration != null ?
                    dateExpiration : LocalDate.now().plusYears(1));
        }

        entrepriseRepository.save(entreprise);

        return entrepriseProfilService.getProfilEntreprise(entrepriseId);
    }

    /**
     * Vérifie si l'employé actuel est un administrateur de l'entreprise spécifiée
     * ou un super admin qui peut gérer toutes les entreprises
     */
    public boolean isAdminOfEntreprise(Long entrepriseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth.getName();

        logger.debug("Authentication principal: {}", principal);

        // Cas 1: Le principal est un ID d'employé (numérique)
        if (principal.matches("\\d+")) {
            try {
                Long employeId = Long.parseLong(principal);
                Optional<Employe> employe = employeRepository.findById(employeId);

                if (employe.isPresent()) {
                    Employe emp = employe.get();
                    // Vérifier si cet employé est un ADMIN pour cette entreprise
                    boolean isAdmin = emp.getRole() == RoleEmploye.ADMIN &&
                            (emp.getEntreprise().getId().equals(entrepriseId));

                    logger.debug("Checking by ID: {}. Is admin of enterprise {}: {}",
                            employeId, entrepriseId, isAdmin);

                    return isAdmin;
                }
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse user ID: {}", principal);
            }
        }

        // Cas 2: Le principal est un email (approche originale)
        String email = principal;

        // Vérifier s'il y a un préfixe dans l'email
        if (email.contains(":")) {
            email = email.substring(email.indexOf(':') + 1);
        }

        // Essayer de trouver un employé qui est admin de cette entreprise spécifique
        Optional<Employe> admin = employeRepository.findByEmailAndEntrepriseIdAndRole(
                email, entrepriseId, RoleEmploye.ADMIN);

        if (admin.isPresent()) {
            logger.debug("Found admin by email: {}", email);
            return true;
        }

        // Si aucun admin spécifique trouvé, vérifions s'il pourrait être
        // un super admin qui peut gérer toutes les entreprises
        Optional<Employe> employe = employeRepository.findByEmail(email);

        if (employe.isPresent() && employe.get().getRole() == RoleEmploye.ADMIN) {
            // Option pour un "super admin" si nécessaire
            // Pour les besoins de test ou une fonctionnalité avancée
            logger.debug("Super admin check for email: {}", email);
            return true;
        }

        logger.warn("Admin check failed for principal: {} on enterprise: {}", principal, entrepriseId);
        return false;
    }
}