package com.recrutement.app.controller;

import com.recrutement.app.dto.MessageDTO;
import com.recrutement.app.model.Conversation;
import com.recrutement.app.model.MessageCandidat;
import com.recrutement.app.model.MessageEntreprise;
import com.recrutement.app.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation API", description = "Endpoints pour la gestion des conversations et messages entre candidats et entreprises")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Obtenir les conversations d'un candidat",
               description = "Retourne une liste paginée des conversations pour un candidat spécifique")
    @ApiResponse(responseCode = "200", description = "Conversations trouvées",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Candidat non trouvé")
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<Page<Conversation>> getConversationsCandidat(
            @Parameter(description = "ID du candidat", example = "1", required = true)
            @PathVariable Long candidatId,
            @Parameter(description = "Paramètres de pagination (size, page, sort)")
            @PageableDefault(size = 10, sort = "derniereMiseAJour") Pageable pageable) {
        Page<Conversation> page = conversationService.getConversationsCandidat(candidatId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Obtenir les conversations d'une entreprise",
               description = "Retourne une liste paginée des conversations pour une entreprise spécifique")
    @ApiResponse(responseCode = "200", description = "Conversations trouvées",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<Page<Conversation>> getConversationsEntreprise(
            @Parameter(description = "ID de l'entreprise", example = "1", required = true)
            @PathVariable Long entrepriseId,
            @Parameter(description = "Paramètres de pagination (size, page, sort)")
            @PageableDefault(size = 10, sort = "derniereMiseAJour") Pageable pageable) {
        Page<Conversation> page = conversationService.getConversationsEntreprise(entrepriseId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Obtenir une conversation spécifique",
               description = "Retourne les détails d'une conversation incluant tous les messages")
    @ApiResponse(responseCode = "200", description = "Conversation trouvée",
                content = @Content(schema = @Schema(implementation = Conversation.class)))
    @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    @GetMapping("/{conversationId}")
    public ResponseEntity<Conversation> getConversation(
            @Parameter(description = "ID de la conversation", example = "1", required = true)
            @PathVariable Long conversationId) {
        Optional<Conversation> conversation = conversationService.getConversationAvecMessages(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtenir les messages d'une conversation",
               description = "Retourne une liste paginée des messages d'une conversation spécifique")
    @ApiResponse(responseCode = "200", description = "Messages trouvés",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessagesConversation(
            @Parameter(description = "ID de la conversation", example = "1", required = true)
            @PathVariable Long conversationId,
            @Parameter(description = "Paramètres de pagination (size, page, sort)")
            @PageableDefault(size = 20, sort = "dateEnvoi") Pageable pageable) {
        
        Optional<Conversation> conversationOpt = conversationService.getConversationAvecMessages(conversationId);
        
        if (!conversationOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Conversation conversation = conversationOpt.get();
        List<MessageDTO> messagesDTO = new ArrayList<>();
        
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
        
        messagesDTO.sort(Comparator.comparing(MessageDTO::getDateEnvoi));
        
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

    @Operation(summary = "Envoyer un message depuis un candidat",
               description = "Permet à un candidat d'envoyer un message à une entreprise")
    @ApiResponse(responseCode = "200", description = "Message envoyé avec succès",
                content = @Content(schema = @Schema(implementation = MessageDTO.class)))
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PostMapping("/candidat/{candidatId}/entreprise/{entrepriseId}/message")
    public ResponseEntity<MessageDTO> envoyerMessageCandidat(
            @Parameter(description = "ID du candidat", example = "1", required = true)
            @PathVariable Long candidatId,
            @Parameter(description = "ID de l'entreprise", example = "1", required = true)
            @PathVariable Long entrepriseId,
            @Parameter(description = "Contenu du message", required = true,
                      schema = @Schema(type = "string", example = "Bonjour, je suis intéressé par votre offre"))
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
        
        messagingTemplate.convertAndSend("/topic/conversations." + message.getConversation().getId(), messageDTO);
        
        return ResponseEntity.ok(messageDTO);
    }

    @Operation(summary = "Envoyer un message depuis une entreprise",
               description = "Permet à une entreprise d'envoyer un message à un candidat")
    @ApiResponse(responseCode = "200", description = "Message envoyé avec succès",
                content = @Content(schema = @Schema(implementation = MessageDTO.class)))
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PostMapping("/entreprise/{entrepriseId}/candidat/{candidatId}/message")
    public ResponseEntity<MessageDTO> envoyerMessageEntreprise(
            @Parameter(description = "ID de l'entreprise", example = "1", required = true)
            @PathVariable Long entrepriseId,
            @Parameter(description = "ID du candidat", example = "1", required = true)
            @PathVariable Long candidatId,
            @Parameter(description = "Contenu du message", required = true,
                      schema = @Schema(type = "string", example = "Nous avons bien reçu votre candidature"))
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
        
        messagingTemplate.convertAndSend("/topic/conversations." + message.getConversation().getId(), messageDTO);
        
        return ResponseEntity.ok(messageDTO);
    }

    @Operation(summary = "Marquer les messages comme lus par une entreprise",
               description = "Met à jour le statut des messages non lus pour une entreprise")
    @ApiResponse(responseCode = "200", description = "Messages marqués comme lus")
    @ApiResponse(responseCode = "404", description = "Conversation ou entreprise non trouvée")
    @PutMapping("/{conversationId}/entreprise/{entrepriseId}/marquer-lu")
    public ResponseEntity<?> marquerMessagesLusPourEntreprise(
            @Parameter(description = "ID de la conversation", example = "1", required = true)
            @PathVariable Long conversationId,
            @Parameter(description = "ID de l'entreprise", example = "1", required = true)
            @PathVariable Long entrepriseId) {
        conversationService.marquerMessagesLusPourEntreprise(conversationId, entrepriseId);
        messagingTemplate.convertAndSend("/topic/conversations." + conversationId + ".status", 
            Map.of("action", "marquer-lu", "type", "entreprise", "entrepriseId", entrepriseId));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marquer les messages comme lus par un candidat",
               description = "Met à jour le statut des messages non lus pour un candidat")
    @ApiResponse(responseCode = "200", description = "Messages marqués comme lus")
    @ApiResponse(responseCode = "404", description = "Conversation ou candidat non trouvé")
    @PutMapping("/{conversationId}/candidat/{candidatId}/marquer-lu")
    public ResponseEntity<?> marquerMessagesLusPourCandidat(
            @Parameter(description = "ID de la conversation", example = "1", required = true)
            @PathVariable Long conversationId,
            @Parameter(description = "ID du candidat", example = "1", required = true)
            @PathVariable Long candidatId) {
        conversationService.marquerMessagesLusPourCandidat(conversationId, candidatId);
        messagingTemplate.convertAndSend("/topic/conversations." + conversationId + ".status", 
            Map.of("action", "marquer-lu", "type", "candidat", "candidatId", candidatId));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Archiver une conversation",
               description = "Change le statut d'une conversation à 'ARCHIVEE'")
    @ApiResponse(responseCode = "200", description = "Conversation archivée")
    @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    @PutMapping("/{conversationId}/archiver")
    public ResponseEntity<?> archiverConversation(
            @Parameter(description = "ID de la conversation", example = "1", required = true)
            @PathVariable Long conversationId) {
        conversationService.archiverConversation(conversationId);
        messagingTemplate.convertAndSend("/topic/conversations." + conversationId + ".status", 
            Map.of("action", "archiver", "conversationId", conversationId));
        return ResponseEntity.ok().build();
    }

    // WebSocket endpoints (non documentés par Swagger)
    @MessageMapping("/send.candidat")
    public void handleMessageFromCandidat(@Payload MessageDTO messageDTO) {
        MessageCandidat message = conversationService.envoyerMessageCandidat(
            messageDTO.getExpediteurId(),
            messageDTO.getDestinataireId(),
            messageDTO.getContenu());
        
        MessageDTO response = new MessageDTO(
            message.getId(),
            message.getContenu(),
            message.getDateEnvoi(),
            message.getEstLu(),
            "CANDIDAT",
            message.getCandidatId(),
            message.getEntrepriseId(),
            message.getConversation().getId()
        );
        
        messagingTemplate.convertAndSend("/topic/conversations." + message.getConversation().getId(), response);
    }

    @MessageMapping("/send.entreprise")
    public void handleMessageFromEntreprise(@Payload MessageDTO messageDTO) {
        MessageEntreprise message = conversationService.envoyerMessageEntreprise(
            messageDTO.getExpediteurId(),
            messageDTO.getDestinataireId(),
            messageDTO.getContenu());
        
        MessageDTO response = new MessageDTO(
            message.getId(),
            message.getContenu(),
            message.getDateEnvoi(),
            message.getEstLu(),
            "ENTREPRISE",
            message.getEntrepriseId(),
            message.getCandidatId(),
            message.getConversation().getId()
        );
        
        messagingTemplate.convertAndSend("/topic/conversations." + message.getConversation().getId(), response);
    }
}