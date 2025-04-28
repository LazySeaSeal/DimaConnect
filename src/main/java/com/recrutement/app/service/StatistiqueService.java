package com.recrutement.app.service;

import com.recrutement.app.dto.StatistiqueDTO;
import com.recrutement.app.mapper.StatistiqueMapper;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.Statistique;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.StatistiqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatistiqueService {

    private final StatistiqueRepository statistiqueRepository;
    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    public StatistiqueService(StatistiqueRepository statistiqueRepository, EntrepriseRepository entrepriseRepository) {
        this.statistiqueRepository = statistiqueRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

    // Save or update Statistique
    public StatistiqueDTO saveStatistique(StatistiqueDTO dto) {
        Entreprise entreprise = entrepriseRepository.findById(dto.getEntrepriseId())
                .orElseThrow(() -> new RuntimeException("Entreprise not found with id: " + dto.getEntrepriseId()));

        Statistique statistique = StatistiqueMapper.toEntity(dto, entreprise);
        Statistique saved = statistiqueRepository.save(statistique);

        return StatistiqueMapper.toDTO(saved);
    }

    // Get all Statistiques
    public List<StatistiqueDTO> getAllStatistiques() {
        return statistiqueRepository.findAll()
                .stream()
                .map(StatistiqueMapper::toDTO)
                .toList();
    }

    // Get Statistique by ID
    public StatistiqueDTO getStatistiqueById(Long id) {
        Statistique statistique = statistiqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Statistique not found with id: " + id));

        return StatistiqueMapper.toDTO(statistique);
    }
    // In StatistiqueService.java
    public boolean existsById(Long id) {
        return statistiqueRepository.existsById(id);
    }


    // Delete Statistique by ID
    public void deleteStatistique(Long id) {
        if (!statistiqueRepository.existsById(id)) {
            throw new RuntimeException("Statistique not found with id: " + id);
        }
        statistiqueRepository.deleteById(id);
    }
}
