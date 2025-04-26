package com.recrutement.app.service;

import com.recrutement.app.model.*;
import com.recrutement.app.model.enums.RoleEmploye;
import com.recrutement.app.model.enums.StatutOffre;
import com.recrutement.app.model.enums.TypeNotification;
import com.recrutement.app.model.enums.TypeContrat;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.CompetenceRepository;
import com.recrutement.app.repository.EmployeRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OffreEmploiService {

    @Autowired
    private OffreEmploiRepository offreEmploiRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Créer une nouvelle offre d'emploi
     */
    @Transactional
    public OffreEmploi creerOffre(OffreEmploi offreEmploi, Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé"));

        offreEmploi.setCreateur(employe);
        offreEmploi.setEntreprise(employe.getEntreprise());
        offreEmploi.setDateCreation(LocalDate.now());

        // Si le créateur est un responsable RH, l'offre est automatiquement validée
        if (employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN) {
            offreEmploi.setStatut(StatutOffre.VALIDEE);
            offreEmploi.setValidateur(employe);
            offreEmploi.setDateValidation(LocalDate.now());
            offreEmploi.setEstActive(true);
        } else {
            // Sinon, elle est en attente de validation
            offreEmploi.setStatut(StatutOffre.EN_ATTENTE);
            offreEmploi.setEstActive(false);

            // Envoyer un email aux responsables RH de l'entreprise
            List<Employe> responsablesRH = employeRepository.findByEntrepriseIdAndRole(
                    employe.getEntreprise().getId(), RoleEmploye.RESPONSABLE_RH);

            for (Employe responsableRH : responsablesRH) {
                emailService.envoyerEmailNouvelleOffre(responsableRH, offreEmploi);
            }
        }

        return offreEmploiRepository.save(offreEmploi);
    }

    /**
     * Valider une offre d'emploi
     */
    @Transactional
    public OffreEmploi validerOffre(Long offreId, Long validateurId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));

        Employe validateur = employeRepository.findById(validateurId)
                .orElseThrow(() -> new EntityNotFoundException("Validateur non trouvé"));

        // Vérifier que le validateur est un responsable RH
        if (validateur.getRole() != RoleEmploye.RESPONSABLE_RH && validateur.getRole() != RoleEmploye.ADMIN) {
            throw new AccessDeniedException("Seul un responsable RH peut valider une offre");
        }

        // Vérifier que l'offre est en attente
        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new IllegalStateException("L'offre n'est pas en attente de validation");
        }

        offre.setStatut(StatutOffre.VALIDEE);
        offre.setValidateur(validateur);
        offre.setDateValidation(LocalDate.now());
        offre.setEstActive(true);

        OffreEmploi offreValidee = offreEmploiRepository.save(offre);

        // Notifier le créateur de l'offre
        emailService.envoyerEmailOffreValidee(offreValidee.getCreateur(), offreValidee);

        // Notifier les candidats correspondant au profil
        notifierCandidatsCorrespondants(offreValidee);

        return offreValidee;
    }

    /**
     * Rejeter une offre d'emploi
     */
    @Transactional
    public OffreEmploi rejeterOffre(Long offreId, Long validateurId, String motifRefus) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));

        Employe validateur = employeRepository.findById(validateurId)
                .orElseThrow(() -> new EntityNotFoundException("Validateur non trouvé"));

        // Vérifier que le validateur est un responsable RH
        if (validateur.getRole() != RoleEmploye.RESPONSABLE_RH && validateur.getRole() != RoleEmploye.ADMIN) {
            throw new AccessDeniedException("Seul un responsable RH peut rejeter une offre");
        }

        // Vérifier que l'offre est en attente
        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new IllegalStateException("L'offre n'est pas en attente de validation");
        }

        offre.setStatut(StatutOffre.REJETEE);
        offre.setValidateur(validateur);
        offre.setMotifRefus(motifRefus);
        offre.setEstActive(false);

        OffreEmploi offreRejetee = offreEmploiRepository.save(offre);

        // Notifier le créateur de l'offre
        emailService.envoyerEmailOffreRejetee(offreRejetee.getCreateur(), offreRejetee);

        return offreRejetee;
    }

    /**
     * Modifier une offre d'emploi
     */
    @Transactional
    public OffreEmploi modifierOffre(Long offreId, OffreEmploi offreDetails, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé"));

        // Vérifier que l'employé a le droit de modifier l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre");
        }

        // Mettre à jour les champs
        offre.setTitre(offreDetails.getTitre());
        offre.setDescription(offreDetails.getDescription());
        offre.setTypeContrat(offreDetails.getTypeContrat());
        offre.setLocalisation(offreDetails.getLocalisation());
        offre.setSalaire(offreDetails.getSalaire());
        offre.setDateExpiration(offreDetails.getDateExpiration());
        offre.setNiveauExpertise(offreDetails.getNiveauExpertise());

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
     * Supprimer une offre d'emploi
     */
    @Transactional
    public void supprimerOffre(Long offreId, Long employeId) {
        OffreEmploi offre = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new EntityNotFoundException("Offre non trouvée"));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé"));

        // Vérifier que l'employé a le droit de supprimer l'offre
        boolean estResponsableRH = employe.getRole() == RoleEmploye.RESPONSABLE_RH || employe.getRole() == RoleEmploye.ADMIN;
        boolean estCreateur = offre.getCreateur().getId().equals(employeId);

        if (!estResponsableRH && !estCreateur) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour supprimer cette offre");
        }

        offreEmploiRepository.delete(offre);
    }

    /**
     * Notifier les candidats correspondant au profil de l'offre
     */
    private void notifierCandidatsCorrespondants(OffreEmploi offre) {
        // Récupérer les compétences requises pour l'offre
        Set<Competence> competencesRequises = offre.getCompetences();

        if (competencesRequises.isEmpty()) {
            return;
        }

        // Rechercher les candidats qui ont ces compétences
        List<Candidat> candidats = candidatRepository.findCandidatsWithCompetences(competencesRequises);

        // Créer une notification pour chaque candidat
        String contenuNotification = String.format(
                "Nouvelle offre d'emploi chez %s: %s",
                offre.getEntreprise().getNom(),
                offre.getTitre()
        );

        for (Candidat candidat : candidats) {
            notificationService.creerNotificationCandidat(
                    candidat,
                    TypeNotification.OFFRE,
                    contenuNotification,
                    offre.getId().toString()
            );
        }
    }

    /**
     * Rechercher des offres avec filtres
     */
    public Page<OffreEmploi> rechercherOffres(
            String titre,
            LocalDate datePublication,
            Integer niveauExpertise,
            String typeContrat,
            String tri,
            int page,
            int taille) {

        // Convertir la chaîne de type de contrat en enum
        TypeContrat typeContratEnum = null;
        if (typeContrat != null && !typeContrat.isEmpty()) {
            try {
                typeContratEnum = TypeContrat.valueOf(typeContrat.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignorer si le type de contrat est invalide
            }
        }

        // Créer le tri
        Sort sort;
        if ("date".equals(tri)) {
            sort = Sort.by(Sort.Direction.DESC, "dateCreation");
        } else {
            // Par défaut, tri par pertinence
            sort = Sort.by(Sort.Direction.DESC, "dateValidation");
        }

        Pageable pageable = PageRequest.of(page, taille, sort);

        return offreEmploiRepository.rechercherOffres(
                titre, datePublication, niveauExpertise, typeContratEnum, pageable);
    }

    /**
     * Obtenir toutes les offres en attente pour une entreprise
     */
    public List<OffreEmploi> getOffresEnAttente(Long entrepriseId) {
        return offreEmploiRepository.findByEntrepriseIdAndStatut(entrepriseId, StatutOffre.EN_ATTENTE);
    }

    /**
     * Obtenir une offre par son ID
     */
    public Optional<OffreEmploi> getOffreById(Long id) {
        return offreEmploiRepository.findById(id);
    }


    /**
     * Récupérer l'ID de l'entreprise à partir de l'ID de l'employé
     */
    public Long getEntrepriseIdFromEmploye(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé avec l'ID: " + employeId));

        return employe.getEntreprise().getId();
    }


    /**
     * Rechercher des offres avec filtres
     */
    /**
     * Recherche avancée d'offres d'emploi avec tous les critères possibles
     */
    public Page<OffreEmploi> rechercheAvancee(
            String titre,
            LocalDate datePublication,
            Integer niveauExpertise,
            String typeContrat,
            Double salaireMini,
            Double salaireMaxi,
            String localisation,
            List<Long> competenceIds,
            String tri,
            int page,
            int taille) {

        // Convertir la chaîne de type de contrat en enum
        TypeContrat typeContratEnum = null;
        if (typeContrat != null && !typeContrat.isEmpty()) {
            try {
                typeContratEnum = TypeContrat.valueOf(typeContrat.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignorer si le type de contrat est invalide
            }
        }

        // Créer le tri
        Sort sort;
        switch (tri) {
            case "date":
                sort = Sort.by(Sort.Direction.DESC, "dateValidation");
                break;
            case "salaire":
                sort = Sort.by(Sort.Direction.DESC, "salaire");
                break;
            case "pertinence":
            default:
                // Tri par défaut (pertinence)
                sort = Sort.by(Sort.Direction.DESC, "dateValidation");
                break;
        }

        Pageable pageable = PageRequest.of(page, taille, sort);

        return offreEmploiRepository.rechercheAvancee(
                titre, datePublication, niveauExpertise, typeContratEnum,
                salaireMini, salaireMaxi, localisation, competenceIds, pageable);
    }

    /**
     * Rechercher des offres par compétences
     */
    public Page<OffreEmploi> rechercherParCompetences(
            List<Long> competenceIds,
            String tri,
            int page,
            int taille) {

        // Créer le tri
        Sort sort;
        if ("date".equals(tri)) {
            sort = Sort.by(Sort.Direction.DESC, "dateValidation");
        } else {
            // Par défaut, tri par pertinence
            sort = Sort.by(Sort.Direction.DESC, "dateValidation");
        }

        Pageable pageable = PageRequest.of(page, taille, sort);

        return offreEmploiRepository.rechercherParCompetences(competenceIds, pageable);
    }
}