package com.recrutement.app.service;

import com.recrutement.app.model.Competence;
import com.recrutement.app.model.CompetenceOffre;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.model.enums.NiveauImportance;
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
import java.util.Optional;
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
     * Ajouter une compétence à une offre d'emploi
     */
    @Transactional
    public CompetenceOffre ajouterCompetence(Long offreId, Long competenceId, CompetenceOffre details, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        Competence competence = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new EntityNotFoundException("Compétence non trouvée avec l'ID: " + competenceId));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Vérifier si la relation existe déjà
        Optional<CompetenceOffre> existingRelation = competenceOffreRepository.findByCompetenceIdAndOffreEmploiId(competenceId, offreId);
        if (existingRelation.isPresent()) {
            throw new IllegalArgumentException("Cette compétence est déjà associée à cette offre");
        }

        // Créer la nouvelle relation
        CompetenceOffre competenceOffre = new CompetenceOffre();
        competenceOffre.setCompetence(competence);
        competenceOffre.setOffreEmploi(offre);

        // Définir les propriétés avec des valeurs par défaut si nécessaires
        competenceOffre.setImportance(details.getImportance() != null ? details.getImportance() : NiveauImportance.BONUS);
        competenceOffre.setNiveau(details.getNiveau() != null ? details.getNiveau() : 1);
        competenceOffre.setEstObligatoire(details.getEstObligatoire() != null ? details.getEstObligatoire() : false);

        CompetenceOffre savedCompetenceOffre = competenceOffreRepository.save(competenceOffre);

        // Ajouter à la collection de l'offre
        offre.getCompetenceOffreDetails().add(savedCompetenceOffre);
        offre.getCompetences().add(competence);
        offreEmploiRepository.save(offre);

        // Mettre à jour le statut de l'offre si nécessaire
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

        return savedCompetenceOffre;
    }

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

            // Vérifier si la relation existe déjà
            Optional<CompetenceOffre> existingRelation = competenceOffreRepository.findByCompetenceIdAndOffreEmploiId(
                    competence.getId(), offreId);

            if (existingRelation.isPresent()) {
                // Mettre à jour la relation existante
                CompetenceOffre existing = existingRelation.get();
                existing.setImportance(competenceOffre.getImportance());
                existing.setNiveau(competenceOffre.getNiveau());
                existing.setEstObligatoire(competenceOffre.getEstObligatoire() != null ?
                        competenceOffre.getEstObligatoire() : false);
                competenceOffreRepository.save(existing);
                continue;
            }

            // S'assurer que tous les champs obligatoires sont définis
            if (competenceOffre.getImportance() == null) {
                competenceOffre.setImportance(NiveauImportance.BONUS); // Valeur par défaut
            }
            if (competenceOffre.getNiveau() == null) {
                competenceOffre.setNiveau(1); // Valeur par défaut
            }
            if (competenceOffre.getEstObligatoire() == null) {
                competenceOffre.setEstObligatoire(false); // Valeur par défaut
            }

            // Initialiser la relation avec l'offre
            competenceOffre.setCompetence(competence);
            competenceOffre.setOffreEmploi(offre);

            // Sauvegarder la compétence d'offre
            CompetenceOffre savedCompetenceOffre = competenceOffreRepository.save(competenceOffre);

            // Ajouter à la collection
            if (offre.getCompetenceOffreDetails() == null) {
                offre.setCompetenceOffreDetails(new HashSet<>());
            }
            offre.getCompetenceOffreDetails().add(savedCompetenceOffre);
        }

        // Synchroniser les deux collections
        offre.synchroniserCompetences();

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
            Long offreId, Long competenceId, CompetenceOffre competenceOffre, Long employeId) {

        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        // Vérifier si la compétence existe pour cette offre
        CompetenceOffre existingCompetence = competenceOffreRepository.findByCompetenceIdAndOffreEmploiId(competenceId, offreId)
                .orElseThrow(() -> new EntityNotFoundException("Relation compétence-offre non trouvée: " + competenceId));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Mettre à jour les propriétés
        existingCompetence.setImportance(competenceOffre.getImportance() != null ?
                competenceOffre.getImportance() : NiveauImportance.BONUS);
        existingCompetence.setNiveau(competenceOffre.getNiveau() != null ?
                competenceOffre.getNiveau() : 1);
        existingCompetence.setEstObligatoire(competenceOffre.getEstObligatoire() != null ?
                competenceOffre.getEstObligatoire() : false);

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
    public void supprimerCompetence(Long offreId, Long competenceId, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        // Trouver la relation par competenceId et offreId
        CompetenceOffre competenceOffre = competenceOffreRepository.findByCompetenceIdAndOffreEmploiId(competenceId, offreId)
                .orElseThrow(() -> new EntityNotFoundException("Relation compétence-offre non trouvée: " + competenceId));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Supprimer la compétence de la liste des compétences de l'offre
        if (offre.getCompetenceOffreDetails() != null) {
            offre.getCompetenceOffreDetails().remove(competenceOffre);
        }

        // Synchroniser les collections
        offre.synchroniserCompetences();

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