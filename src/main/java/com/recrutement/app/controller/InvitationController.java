package com.recrutement.app.controller;

import com.recrutement.app.dto.InvitationDTO;
import com.recrutement.app.dto.InscriptionEmployeDTO;
import com.recrutement.app.model.Employe;
import com.recrutement.app.model.InvitationEmploye;
import com.recrutement.app.service.InvitationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping
    public ResponseEntity<InvitationEmploye> inviterEmploye(@Valid @RequestBody InvitationDTO invitationDTO) {
        InvitationEmploye invitation = invitationService.creerInvitation(invitationDTO);
        return new ResponseEntity<>(invitation, HttpStatus.CREATED);
    }

    @PostMapping("/inscription")
    public ResponseEntity<Employe> finaliserInscription(@Valid @RequestBody InscriptionEmployeDTO inscriptionDTO) {
        Employe employe = invitationService.finaliserInscription(inscriptionDTO);
        return new ResponseEntity<>(employe, HttpStatus.CREATED);
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<InvitationEmploye>> getInvitationsActives(@PathVariable Long entrepriseId) {
        List<InvitationEmploye> invitations = invitationService.getInvitationsActives(entrepriseId);
        return ResponseEntity.ok(invitations);
    }
}