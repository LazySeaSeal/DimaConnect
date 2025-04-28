package com.recrutement.app.service;

import com.recrutement.app.dto.PaiementDTO;
import com.recrutement.app.mapper.PaiementMapper;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.Paiement;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    public PaiementService(PaiementRepository paiementRepository, EntrepriseRepository entrepriseRepository) {
        this.paiementRepository = paiementRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    // Create or Update Paiement
    public PaiementDTO savePaiement(PaiementDTO dto) {
        Entreprise entreprise = entrepriseRepository.findById(dto.getEntrepriseId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable avec ID : " + dto.getEntrepriseId()));

        Paiement paiement = PaiementMapper.toEntity(dto, entreprise);
        Paiement savedPaiement = paiementRepository.save(paiement);
        return PaiementMapper.toDTO(savedPaiement);
    }
    public boolean existsById(Long id) {
        return paiementRepository.existsById(id);
    }


    // Get all paiements
    public List<PaiementDTO> getAllPaiements() {
        return paiementRepository.findAll()
                .stream()
                .map(PaiementMapper::toDTO)
                .toList();
    }

    // Get paiement by ID
    public Optional<PaiementDTO> getPaiementById(Long id) {
        Optional<Paiement> paiement = paiementRepository.findById(id);
        return paiement.map(PaiementMapper::toDTO); // Map it to PaiementDTO if present
    }


    // Delete paiement by ID
    public void deletePaiement(Long id) {
        if (!paiementRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Paiement introuvable avec ID : " + id);
        }
        paiementRepository.deleteById(id);
    }
}
