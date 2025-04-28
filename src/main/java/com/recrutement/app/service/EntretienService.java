package com.recrutement.app.service;

import com.recrutement.app.dto.EntretienRequestDTO;
import com.recrutement.app.dto.EntretienResponseDTO;
import com.recrutement.app.model.Entretien;
import com.recrutement.app.model.Candidature;
import com.recrutement.app.repository.EntretienRepository;
import com.recrutement.app.repository.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntretienService {

    private final EntretienRepository entretienRepository;
    private final CandidatureRepository candidatureRepository;
    private final ModelMapper modelMapper; // Ensure you configure ModelMapper as a Bean in your project

    // set date il entretien planificiation
    public EntretienResponseDTO planifierEntretien(EntretienRequestDTO entretienDTO) {
        if (entretienDTO.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de l'entretien doit être dans le futur.");
        }

        Candidature candidature = candidatureRepository.findById(entretienDTO.getCandidatureId())
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        Entretien entretien = modelMapper.map(entretienDTO, Entretien.class);
        entretien.setCandidature(candidature);
        Entretien savedEntretien = entretienRepository.save(entretien);
        return modelMapper.map(savedEntretien, EntretienResponseDTO.class);
    }

    // Modifier un entretien
    public EntretienResponseDTO modifierEntretien(Long entretienId, EntretienRequestDTO entretienDTO) {
        Entretien entretien = entretienRepository.findById(entretienId)
                .orElseThrow(() -> new IllegalArgumentException("Entretien introuvable"));

        // nmappiw lil dto
        entretien.setDate(entretienDTO.getDate());
        entretien.setLien(entretienDTO.getLienVisio());
        entretien.setDuree(entretienDTO.getDuree());
        entretien.setType(entretienDTO.getTypeEntretien());
        entretien.setEstVisioConference(entretienDTO.getEstVisioConference());

        Entretien updatedEntretien = entretienRepository.save(entretien);

        return modelMapper.map(updatedEntretien, EntretienResponseDTO.class);
    }

    // Supprimer un entretien
    public void supprimerEntretien(Long entretienId) {
        if (!entretienRepository.existsById(entretienId)) {
            throw new IllegalArgumentException("Entretien introuvable");
        }
        entretienRepository.deleteById(entretienId);
    }

    // entretien by id candidait
    public List<EntretienResponseDTO> getEntretiensByCandidatId(Long candidatId) {
        List<Entretien> entretiens = entretienRepository.findByPostulantId(candidatId);

        return entretiens.stream()
                .map(entretien -> modelMapper.map(entretien, EntretienResponseDTO.class))
                .collect(Collectors.toList());
    }

    // entretien automatic ba3ed 3 days najmou nrodhma variable w customizable (if need w abraà)
    public EntretienResponseDTO planificationAutomatique(EntretienRequestDTO entretienDTO) {
        Entretien entretien = modelMapper.map(entretienDTO, Entretien.class);
        LocalDate dateAutomatique = LocalDate.now().plusDays(3);
        entretien.setDate(dateAutomatique);

        Entretien savedEntretien = entretienRepository.save(entretien);

        return modelMapper.map(savedEntretien, EntretienResponseDTO.class);
    }
}