package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatAbonneDTO;
import com.recrutement.app.model.Abonnement;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.AbonnementRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbonnesEntrepriseService {

    private final AbonnementRepository abonnementRepository;
    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    public AbonnesEntrepriseService(AbonnementRepository abonnementRepository, EntrepriseRepository entrepriseRepository) {
        this.abonnementRepository = abonnementRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    @Transactional(readOnly = true)
    public List<CandidatAbonneDTO> getAbonnesEntreprise(Long entrepriseId) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);

        if (entrepriseOpt.isEmpty()) {
            throw new RuntimeException("Entreprise non trouvée avec l'ID: " + entrepriseId);
        }

        List<Abonnement> abonnements = abonnementRepository.findByEntrepriseId(entrepriseId);

        return abonnements.stream()
                .map(abonnement -> CandidatAbonneDTO.fromCandidat(
                        abonnement.getCandidat(),
                        abonnement.getDateAbonnement()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isAbonne(Long entrepriseId, Long candidatId) {
        return abonnementRepository.existsByEntrepriseIdAndCandidatId(entrepriseId, candidatId);
    }
}