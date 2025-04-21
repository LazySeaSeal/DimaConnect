package com.recrutement.app.controller;

import com.recrutement.app.dto.MessageCandidatToCandidatDTO;
import com.recrutement.app.model.ConversationCandidat;
import com.recrutement.app.model.MessageCandidatToCandidat;
import com.recrutement.app.service.ConversationCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@RestController
@RequestMapping("/api/conversations/candidat-to-candidat") // Changed from /candidat to /candidat-to-candidat
public class ConversationCandidatController {

    @Autowired
    private ConversationCandidatService conversationCandidatService;

    @PostMapping("/message")
    public ResponseEntity<MessageCandidatToCandidatDTO> envoyerMessage(
            @RequestBody MessageCandidatToCandidatDTO messageDTO) {
        MessageCandidatToCandidat message = conversationCandidatService.envoyerMessage(
                messageDTO.getExpediteurId(),
                messageDTO.getDestinataireId(),
                messageDTO.getContenu());
        
        MessageCandidatToCandidatDTO responseDTO = new MessageCandidatToCandidatDTO(
                message.getId(),
                message.getContenu(),
                message.getDateEnvoi(),
                message.getEstLu(),
                message.getExpediteurId(),
                message.getDestinataireId(),
                message.getConversation().getId());
        
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{candidatId}")
    public ResponseEntity<Page<ConversationCandidat>> getConversations(
            @PathVariable Long candidatId, Pageable pageable) {
        return ResponseEntity.ok(conversationCandidatService.getConversationsCandidat(candidatId, pageable));
    }

    @GetMapping("/details/{conversationId}")
    public ResponseEntity<ConversationCandidat> getConversation(
            @PathVariable Long conversationId) {
        Optional<ConversationCandidat> conversation = conversationCandidatService.getConversationAvecMessages(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/lire/{conversationId}/{destinataireId}")
    public ResponseEntity<Void> marquerMessagesLus(
            @PathVariable Long conversationId, @PathVariable Long destinataireId) {
        conversationCandidatService.marquerMessagesLus(conversationId, destinataireId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/archiver/{conversationId}")
    public ResponseEntity<Void> archiverConversation(
            @PathVariable Long conversationId) {
        conversationCandidatService.archiverConversation(conversationId);
        return ResponseEntity.ok().build();
    }
}