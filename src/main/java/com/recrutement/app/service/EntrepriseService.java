package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.dto.EntrepriseDto;
import com.recrutement.app.dto.OffreEmploiDto;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.model.OffreEmploi;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import com.recrutement.app.repository.OffreEmploiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.recrutement.app.exception.EntityNotFoundException;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CandidatRepository candidatRepository;
    private final ModelMapper modelMapper;
    public List<EntrepriseDto> searchEntreprises(String keyword) {
        return entrepriseRepository.findByNomContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EntrepriseDto getEntrepriseDetails(Long id) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(id);
        if (entrepriseOpt.isEmpty()) {
            throw new EntityNotFoundException("Entreprise not found");
        }
        Entreprise entreprise = entrepriseOpt.get();

        List<OffreEmploi> offres = offreEmploiRepository.findByEntrepriseAndEstActiveTrue(entreprise);
        List<OffreEmploiDto> offreDtos = offres.stream()
                .map(offreEmploi -> modelMapper.map(offreEmploi, OffreEmploiDto.class))
                .collect(Collectors.toList());

        EntrepriseDto entrepriseDto = modelMapper.map(entreprise, EntrepriseDto.class);
        entrepriseDto.setOffres(offreDtos);

        return entrepriseDto;
    }

    public List<CandidatProfileDTO> getContactsInEntreprise(Long entrepriseId, Long candidatId) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(entrepriseId);
        if (entrepriseOpt.isEmpty()) {
            throw new EntityNotFoundException("Entreprise not found");
        }
        Optional<Candidat> candidatOpt = candidatRepository.findById(candidatId);
        if (candidatOpt.isEmpty()) {
            throw new EntityNotFoundException("Candidat not found");
        }

        Entreprise entreprise = entrepriseOpt.get();
        Candidat candidat = candidatOpt.get();

        List<Candidat> contacts = entrepriseRepository.findContactsInEntreprise(candidat, entreprise);
        return contacts.stream()
                .map(contact -> modelMapper.map(contact, CandidatProfileDTO.class))
                .collect(Collectors.toList());
    }

    private EntrepriseDto mapToDto(Entreprise entreprise) {
        EntrepriseDto dto = new EntrepriseDto();
        dto.setId(entreprise.getId());
        dto.setNom(entreprise.getNom());
        dto.setEmail(entreprise.getEmail());
        dto.setDescription(entreprise.getDescription());
        dto.setSecteurActivite(entreprise.getSecteurActivite());
        dto.setUrlSite(entreprise.getUrlSite());
        return dto;
    }
}
