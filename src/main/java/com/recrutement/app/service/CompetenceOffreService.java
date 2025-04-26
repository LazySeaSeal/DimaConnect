package com.recrutement.app.service;

import com.recrutement.app.model.Competence;
import com.recrutement.app.model.CompetenceOffre;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.RoleEmploye;
import com.recrutement.app.model.enums.StatutOffre;
import com.recrutement.app.repository.CompetenceOffreRepository;
import com.recrutement.app.repository.CompetenceRepository;
import com.recrutement.app.repository.EmployeRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompetenceOffreService {

    @Autowired
    private OffreEmploiRepository offreEmploiRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CompetenceOffreRepository competenceOffreRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Ajouter des compétences à une offre d'emploi
     */
    @Transactional
    public OffreEmploi ajouterCompetences(Long offreId, List<CompetenceOffre> competences, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Traiter chaque compétence
        for (CompetenceOffre competenceOffre : competences) {
            // Vérifier si la compétence existe
            Competence competence = competenceRepository.findById(competenceOffre.getCompetence().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Compétence non trouvée: " +
                            competenceOffre.getCompetence().getId()));

            // S'assurer que tous les champs obligatoires sont définis
            if (competenceOffre.getImportance() == null) {
                throw new IllegalArgumentException("Le niveau d'importance est requis pour la compétence: " + competence.getNom());
            }
            if (competenceOffre.getNiveau() == null) {
                throw new IllegalArgumentException("Le niveau d'expertise est requis pour la compétence: " + competence.getNom());
            }
            if (competenceOffre.getEstObligatoire() == null) {
                competenceOffre
                        .setEstObligatoire(false); // Valeur par défaut
            }

            // Initialiser la relation avec l'offre
            competenceOffre.setCompetence(competence);
            competenceOffre.setOffreEmploi(offre);

            // Sauvegarder la compétence d'offre
            CompetenceOffre savedCompetenceOffre = competenceOffreRepository.save(competenceOffre);

            // Ajouter à la collection
            offre.getCompetenceOffreDetails().add(savedCompetenceOffre);

            // Ajouter également à la collection simplifiée pour la rétrocompatibilité
            //offre.getCompetences().add(competence);
        }

        // Si on est chef d'équipe et que l'offre était déjà validée,
        // il faut la remettre en attente de validation
        if (!estResponsableRH && (offre.getStatut() == StatutOffre.VALIDEE || offre.getStatut() == StatutOffre.PUBLIEE)) {
            offre.setStatut(StatutOffre.EN_ATTENTE);
            offre.setEstActive(false);

            // Notifier les responsables RH
            List<Employe> responsablesRH = employeRepository.findByEntrepriseIdAndRole(
                    employe.getEntreprise().getId(), RoleEmploye.RESPONSABLE_RH);

            for (Employe responsableRH : responsablesRH) {
                emailService.envoyerEmailModificationOffre(responsableRH, offre);
            }
        }

        return offreEmploiRepository.save(offre);
    }

    /**
     * Récupérer toutes les compétences d'une offre
     */
    public Set<CompetenceOffre> getCompetencesParOffre(Long offreId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        return offre.getCompetenceOffreDetails();
    }

    /**
     * Mettre à jour une compétence spécifique de l'offre
     */
    @Transactional
    public CompetenceOffre mettreAJourCompetence(
            Long offreId, Long competenceOffreId, CompetenceOffre competenceOffre, Long employeId) {

        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        CompetenceOffre existingCompetence = competenceOffreRepository.findById(competenceOffreId)
                .orElseThrow(() -> new EntityNotFoundException("Relation compétence-offre non trouvée: " + competenceOffreId));

        // Vérifier que cette compétence appartient bien à cette offre
        if (!existingCompetence.getOffreEmploi().getId().equals(offreId)) {
            throw new IllegalArgumentException("Cette compétence n'appartient pas à l'offre spécifiée");
        }

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Mettre à jour les propriétés
        existingCompetence.setImportance(competenceOffre.getImportance());
        existingCompetence.setNiveau(competenceOffre.getNiveau());
        existingCompetence.setEstObligatoire(competenceOffre.getEstObligatoire());

        // Si on est chef d'équipe et que l'offre était déjà validée,
        // il faut la remettre en attente de validation
        if (!estResponsableRH && (offre.getStatut() == StatutOffre.VALIDEE || offre.getStatut() == StatutOffre.PUBLIEE)) {
            offre.setStatut(StatutOffre.EN_ATTENTE);
            offre.setEstActive(false);
            offreEmploiRepository.save(offre);

            // Notifier les responsables RH
            List<Employe> responsablesRH = employeRepository.findByEntrepriseIdAndRole(
                    employe.getEntreprise().getId(), RoleEmploye.RESPONSABLE_RH);

            for (Employe responsableRH : responsablesRH) {
                emailService.envoyerEmailModificationOffre(responsableRH, offre);
            }
        }

        return competenceOffreRepository.save(existingCompetence);
    }

    /**
     * Supprimer une compétence de l'offre
     */
    @Transactional
    public void supprimerCompetence(Long offreId, Long competenceOffreId, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        CompetenceOffre competenceOffre = competenceOffreRepository.findById(competenceOffreId)
                .orElseThrow(() -> new EntityNotFoundException("Relation compétence-offre non trouvée: " + competenceOffreId));

        // Vérifier que cette compétence appartient bien à cette offre
        if (!competenceOffre.getOffreEmploi().getId().equals(offreId)) {
            throw new IllegalArgumentException("Cette compétence n'appartient pas à l'offre spécifiée");
        }

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Supprimer la compétence de la liste des compétences de l'offre
        offre.getCompetenceOffreDetails().remove(competenceOffre);
        offre.getCompetences().remove(competenceOffre.getCompetence());

        // Si on est chef d'équipe et que l'offre était déjà validée,
        // il faut la remettre en attente de validation
        if (!estResponsableRH && (offre.getStatut() == StatutOffre.VALIDEE || offre.getStatut() == StatutOffre.PUBLIEE)) {
            offre.setStatut(StatutOffre.EN_ATTENTE);
            offre.setEstActive(false);

            // Notifier les responsables RH
            List<Employe> responsablesRH = employeRepository.findByEntrepriseIdAndRole(
                    employe.getEntreprise().getId(), RoleEmploye.RESPONSABLE_RH);

            for (Employe responsableRH : responsablesRH) {
                emailService.envoyerEmailModificationOffre(responsableRH, offre);
            }
        }

        offreEmploiRepository.save(offre);
        competenceOffreRepository.delete(competenceOffre);
    }
}