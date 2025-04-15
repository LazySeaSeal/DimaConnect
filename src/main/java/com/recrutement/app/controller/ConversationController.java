package com.recrutement.app.controller;

import com.recrutement.app.dto.MessageDTO;
import com.recrutement.app.model.Conversation;
import com.recrutement.app.model.MessageCandidat;
import com.recrutement.app.model.MessageEntreprise;
import com.recrutement.app.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/candidat/{candidatId}")
    public Page<Conversation> getConversationsCandidat(
            @PathVariable Long candidatId,
            @PageableDefault(size = 10, sort = "derniereMiseAJour") Pageable pageable) {
        return conversationService.getConversationsCandidat(candidatId, pageable);
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public Page<Conversation> getConversationsEntreprise(
            @PathVariable Long entrepriseId,
            @PageableDefault(size = 10, sort = "derniereMiseAJour") Pageable pageable) {
        return conversationService.getConversationsEntreprise(entrepriseId, pageable);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long conversationId) {
        Optional<Conversation> conversation = conversationService.getConversationAvecMessages(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessagesConversation(
            @PathVariable Long conversationId,
            @PageableDefault(size = 20, sort = "dateEnvoi") Pageable pageable) {
        
        Optional<Conversation> conversationOpt = conversationService.getConversationAvecMessages(conversationId);
        
        if (!conversationOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Conversation conversation = conversationOpt.get();
        List<MessageDTO> messagesDTO = new ArrayList<>();
        
        // Convert MessageCandidat to DTO
        if (conversation.getMessagesCandidat() != null) {
            messagesDTO.addAll(conversation.getMessagesCandidat().stream()
                .map(msg -> new MessageDTO(
                    msg.getId(),
                    msg.getContenu(),
                    msg.getDateEnvoi(),
                    msg.getEstLu(),
                    "CANDIDAT",
                    msg.getCandidatId(),
                    msg.getEntrepriseId(),
                    conversationId
                ))
                .collect(Collectors.toList()));
        }
        
        // Convert MessageEntreprise to DTO
        if (conversation.getMessagesEntreprise() != null) {
            messagesDTO.addAll(conversation.getMessagesEntreprise().stream()
                .map(msg -> new MessageDTO(
                    msg.getId(),
                    msg.getContenu(),
                    msg.getDateEnvoi(),
                    msg.getEstLu(),
                    "ENTREPRISE",
                    msg.getEntrepriseId(),
                    msg.getCandidatId(),
                    conversationId
                ))
                .collect(Collectors.toList()));
        }
        
        // Sort by date
        messagesDTO.sort(Comparator.comparing(MessageDTO::getDateEnvoi));
        
        // Pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), messagesDTO.size());
        
        if (start > messagesDTO.size()) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), pageable, messagesDTO.size()));
        }
        
        Page<MessageDTO> page = new PageImpl<>(
            messagesDTO.subList(start, end), 
            pageable, 
            messagesDTO.size()
        );
        
        return ResponseEntity.ok(page);
    }

    @PostMapping("/candidat/{candidatId}/entreprise/{entrepriseId}/message")
    public ResponseEntity<MessageDTO> envoyerMessageCandidat(
            @PathVariable Long candidatId,
            @PathVariable Long entrepriseId,
            @RequestBody String contenu) {
        
        MessageCandidat message = conversationService.envoyerMessageCandidat(candidatId, entrepriseId, contenu);
        
        MessageDTO messageDTO = new MessageDTO(
            message.getId(),
            message.getContenu(),
            message.getDateEnvoi(),
            message.getEstLu(),
            "CANDIDAT",
            message.getCandidatId(),
            message.getEntrepriseId(),
            message.getConversation().getId()
        );
        
        return ResponseEntity.ok(messageDTO);
    }

    @PostMapping("/entreprise/{entrepriseId}/candidat/{candidatId}/message")
    public ResponseEntity<MessageDTO> envoyerMessageEntreprise(
            @PathVariable Long entrepriseId,
            @PathVariable Long candidatId,
            @RequestBody String contenu) {
        
        MessageEntreprise message = conversationService.envoyerMessageEntreprise(entrepriseId, candidatId, contenu);
        
        MessageDTO messageDTO = new MessageDTO(
            message.getId(),
            message.getContenu(),
            message.getDateEnvoi(),
            message.getEstLu(),
            "ENTREPRISE",
            message.getEntrepriseId(),
            message.getCandidatId(),
            message.getConversation().getId()
        );
        
        return ResponseEntity.ok(messageDTO);
    }

    @PutMapping("/{conversationId}/entreprise/{entrepriseId}/marquer-lu")
    public ResponseEntity<?> marquerMessagesLusPourEntreprise(
            @PathVariable Long conversationId,
            @PathVariable Long entrepriseId) {
        
        conversationService.marquerMessagesLusPourEntreprise(conversationId, entrepriseId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{conversationId}/candidat/{candidatId}/marquer-lu")
    public ResponseEntity<?> marquerMessagesLusPourCandidat(
            @PathVariable Long conversationId,
            @PathVariable Long candidatId) {
        
        conversationService.marquerMessagesLusPourCandidat(conversationId, candidatId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{conversationId}/archiver")
    public ResponseEntity<?> archiverConversation(@PathVariable Long conversationId) {
        conversationService.archiverConversation(conversationId);
        return ResponseEntity.ok().build();
    }
}