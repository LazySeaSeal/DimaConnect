package com.recrutement.app.service;

import com.recrutement.app.dto.CandidatProfileDTO;
import com.recrutement.app.dto.ContactRequestDTO;
import com.recrutement.app.dto.ContactSearchDTO;
import com.recrutement.app.exception.ResourceNotFoundException;
import com.recrutement.app.mapper.CandidatMapper;
import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.ContactCandidat;
import com.recrutement.app.model.enums.StatutContact;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.ContactCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkingService {

    private final CandidatRepository candidatRepository;
    private final ContactCandidatRepository contactCandidatRepository;
    private final CandidatMapper candidatMapper;

    @Autowired
    public NetworkingService(CandidatRepository candidatRepository, ContactCandidatRepository contactCandidatRepository, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.contactCandidatRepository = contactCandidatRepository;
        this.candidatMapper = candidatMapper;
    }

    public List<CandidatProfileDTO> searchContacts(ContactSearchDTO searchDTO) {
        switch (searchDTO.getSearchType()) {
            case "NAME":
                return candidatRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                                searchDTO.getSearchTerm(), searchDTO.getSearchTerm())
                        .stream()
                        .map(candidatMapper::toProfileDTO)
                        .collect(Collectors.toList());
            case "SKILL":
                return candidatRepository.findByCompetences_NomContainingIgnoreCase(searchDTO.getSearchTerm())
                        .stream()
                        .map(candidatMapper::toProfileDTO)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }

    @Transactional
    public void sendContactRequest(ContactRequestDTO requestDTO) {
        if (requestDTO.getSenderId().equals(requestDTO.getReceiverId())) {
            throw new IllegalArgumentException("Cannot send contact request to yourself");
        }

        if (contactCandidatRepository.existsBySenderIdAndReceiverId(
                requestDTO.getSenderId(), requestDTO.getReceiverId())) {
            throw new IllegalArgumentException("Contact request already exists between these candidates");
        }

        Candidat sender = candidatRepository.findById(requestDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender candidate not found"));
        Candidat receiver = candidatRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver candidate not found"));

        ContactCandidat contact = new ContactCandidat();
        contact.setSender(sender);
        contact.setReceiver(receiver);
        contact.setDateConnexion(LocalDate.now());
        contact.setStatut(StatutContact.EN_ATTENTE);

        contactCandidatRepository.save(contact);
    }

    @Transactional
    public void acceptContactRequest(Long contactId) {
        ContactCandidat contact = contactCandidatRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact request not found"));

        contact.setStatut(StatutContact.ACCEPTE);
        contactCandidatRepository.save(contact);

        if (!contactCandidatRepository.existsBySenderIdAndReceiverId(
                contact.getReceiver().getId(), contact.getSender().getId())) {
            ContactCandidat reverseContact = new ContactCandidat();
            reverseContact.setSender(contact.getReceiver());
            reverseContact.setReceiver(contact.getSender());
            reverseContact.setDateConnexion(LocalDate.now());
            reverseContact.setStatut(StatutContact.ACCEPTE);
            contactCandidatRepository.save(reverseContact);
        }
    }

    @Transactional
    public void rejectContactRequest(Long contactId) {
        ContactCandidat contact = contactCandidatRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact request not found"));

        contactCandidatRepository.delete(contact);

        contactCandidatRepository.findBySenderIdAndReceiverId(
                contact.getReceiver().getId(), contact.getSender().getId()
        ).ifPresent(contactCandidatRepository::delete);
    }

    public List<CandidatProfileDTO> getConnections(Long candidatId) {
        List<ContactCandidat> connections = contactCandidatRepository
                .findBySenderIdOrReceiverIdAndStatut(candidatId, candidatId, StatutContact.ACCEPTE);

        return connections.stream()
                .map(contact -> contact.getSender().getId().equals(candidatId)
                        ? candidatMapper.toProfileDTO(contact.getReceiver())
                        : candidatMapper.toProfileDTO(contact.getSender()))
                .collect(Collectors.toList());
    }
}
