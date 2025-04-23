package com.recrutement.app.controller;
import com.recrutement.app.dto.MessageEntrepriseToEntrepriseDTO;
import com.recrutement.app.model.ConversationEntreprise;
import com.recrutement.app.model.MessageEntrepriseToEntreprise;
import com.recrutement.app.service.ConversationEntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations/entreprise-to-entreprise")
public class ConversationEntrepriseController {
    @Autowired
    private ConversationEntrepriseService conversationEntrepriseService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
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
        
        // Envoyer notification WebSocket
        messagingTemplate.convertAndSend(
                "/topic/conversations/entreprise/" + message.getDestinataireId(), 
                responseDTO);
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @MessageMapping("/envoyer-message-entreprise")
    @SendTo("/topic/conversations/entreprise")
    public MessageEntrepriseToEntrepriseDTO envoyerMessageWebSocket(MessageEntrepriseToEntrepriseDTO messageDTO) {
        MessageEntrepriseToEntreprise message = conversationEntrepriseService.envoyerMessage(
                messageDTO.getExpediteurId(),
                messageDTO.getDestinataireId(),
                messageDTO.getContenu());
        
        return new MessageEntrepriseToEntrepriseDTO(
                message.getId(),
                message.getContenu(),
                message.getDateEnvoi(),
                message.getEstLu(),
                message.getExpediteurId(),
                message.getDestinataireId(),
                message.getConversation().getId());
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
        
        // Envoyer notification WebSocket pour la mise à jour du statut de lecture
        messagingTemplate.convertAndSend(
                "/topic/conversations/entreprise/status/" + conversationId,
                Map.of("conversationId", conversationId, "status", "read", "destinataireId", destinataireId));
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/archiver/{conversationId}")
    public ResponseEntity<Void> archiverConversation(
            @PathVariable Long conversationId) {
        conversationEntrepriseService.archiverConversation(conversationId);
        
        // Envoyer notification WebSocket pour la mise à jour du statut d'archivage
        messagingTemplate.convertAndSend(
                "/topic/conversations/entreprise/status/" + conversationId,
                Map.of("conversationId", conversationId, "status", "archived"));
        
        return ResponseEntity.ok().build();
    }
}