package com.recrutement.app.service;
import java.util.List;
import java.util.stream.Collectors;
import com.recrutement.app.dto.EntrepriseDto;

import com.recrutement.app.dto.AbonnementRequest;
import com.recrutement.app.model.Abonnement;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.AbonnementRepository;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final CandidatRepository candidatRepository;
    private final EntrepriseRepository entrepriseRepository;

    public void subscribeToEntreprise(AbonnementRequest request) {
        Candidat candidat = candidatRepository.findById(request.getCandidatId())
                .orElseThrow(() -> new RuntimeException("Candidat not found"));

        Entreprise entreprise = entrepriseRepository.findById(request.getEntrepriseId())
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        if (abonnementRepository.existsByCandidatAndEntreprise(candidat, entreprise)) {
            throw new RuntimeException("Déjà abonné à cette entreprise");
        }

        Abonnement abonnement = new Abonnement();
        abonnement.setCandidat(candidat);
        abonnement.setEntreprise(entreprise);
        abonnement.setDateAbonnement(LocalDate.now());

        abonnementRepository.save(abonnement);
    }

    public List<EntrepriseDto> getAbonnementsByCandidat(Long candidatId) {
        List<Abonnement> abonnements = abonnementRepository.findByCandidatId(candidatId);
        return abonnements.stream()
                .map(ab -> mapToEntrepriseDto(ab.getEntreprise()))
                .collect(Collectors.toList());
    }

    private EntrepriseDto mapToEntrepriseDto(Entreprise entreprise) {
        EntrepriseDto dto = new EntrepriseDto();
        dto.setId(entreprise.getId());
        dto.setNom(entreprise.getNom());
        dto.setEmail(entreprise.getEmail());
        dto.setSecteurActivite(entreprise.getSecteurActivite());
        dto.setDescription(entreprise.getDescription());
        dto.setUrlSite(entreprise.getUrlSite());
        return dto;
    }
}
