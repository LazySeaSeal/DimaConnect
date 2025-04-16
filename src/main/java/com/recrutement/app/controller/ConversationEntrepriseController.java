package com.recrutement.app.controller;

import com.recrutement.app.dto.MessageEntrepriseToEntrepriseDTO;
import com.recrutement.app.model.ConversationEntreprise;
import com.recrutement.app.model.MessageEntrepriseToEntreprise;
import com.recrutement.app.service.ConversationEntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/conversations/entreprise-to-entreprise") // Changed from /entreprise to /entreprise-to-entreprise
public class ConversationEntrepriseController {

    @Autowired
    private ConversationEntrepriseService conversationEntrepriseService;

    @PostMapping("/message")
    public ResponseEntity<MessageEntrepriseToEntrepriseDTO> envoyerMessage(
            @RequestBody MessageEntrepriseToEntrepriseDTO messageDTO) {
        MessageEntrepriseToEntreprise message = conversationEntrepriseService.envoyerMessage(
                messageDTO.getExpediteurId(),
                messageDTO.getDestinataireId(),
                messageDTO.getContenu());
        
        MessageEntrepriseToEntrepriseDTO responseDTO = new MessageEntrepriseToEntrepriseDTO(
                message.getId(),
                message.getContenu(),
                message.getDateEnvoi(),
                message.getEstLu(),
                message.getExpediteurId(),
                message.getDestinataireId(),
                message.getConversation().getId());
        
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{entrepriseId}")
    public ResponseEntity<Page<ConversationEntreprise>> getConversations(
            @PathVariable Long entrepriseId, Pageable pageable) {
        return ResponseEntity.ok(conversationEntrepriseService.getConversationsEntreprise(entrepriseId, pageable));
    }

    @GetMapping("/details/{conversationId}")
    public ResponseEntity<ConversationEntreprise> getConversation(
            @PathVariable Long conversationId) {
        Optional<ConversationEntreprise> conversation = conversationEntrepriseService.getConversationAvecMessages(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/lire/{conversationId}/{destinataireId}")
    public ResponseEntity<Void> marquerMessagesLus(
            @PathVariable Long conversationId, @PathVariable Long destinataireId) {
        conversationEntrepriseService.marquerMessagesLus(conversationId, destinataireId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/archiver/{conversationId}")
    public ResponseEntity<Void> archiverConversation(
            @PathVariable Long conversationId) {
        conversationEntrepriseService.archiverConversation(conversationId);
        return ResponseEntity.ok().build();
    }
}