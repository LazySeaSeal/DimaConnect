package com.recrutement.app.controller;

import com.recrutement.app.dto.StatistiqueDTO;
import com.recrutement.app.exception.StatistiqueNotFoundException; // import the custom exception
import com.recrutement.app.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @Autowired
    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    // Create a new statistique
    @PostMapping
    public StatistiqueDTO createStatistique(@RequestBody StatistiqueDTO statistiqueDTO) {
        return statistiqueService.saveStatistique(statistiqueDTO);
    }

    // Get all statistiques
    @GetMapping
    public List<StatistiqueDTO> getAllStatistiques() {
        return statistiqueService.getAllStatistiques();
    }

    // Get statistique by ID
    @GetMapping("/{id}")
    public StatistiqueDTO getStatistiqueById(@PathVariable Long id) {
        StatistiqueDTO statistiqueDTO = statistiqueService.getStatistiqueById(id);
        if (statistiqueDTO == null) {
            throw new StatistiqueNotFoundException("Statistique with ID " + id + " not found!");
        }
        return statistiqueDTO;
    }

    // Delete statistique by ID
    @DeleteMapping("/{id}")
    public void deleteStatistique(@PathVariable Long id) {
        if (!statistiqueService.existsById(id)) {
            throw new StatistiqueNotFoundException("Statistique with ID " + id + " not found!");
        }
        statistiqueService.deleteStatistique(id);
    }
}
