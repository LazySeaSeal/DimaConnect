package com.recrutement.app.service;

import com.recrutement.app.model.ConversationCandidat;
import com.recrutement.app.model.MessageCandidatToCandidat;
import com.recrutement.app.repository.ConversationCandidatRepository;
import com.recrutement.app.repository.MessageCandidatToCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationCandidatService {

    @Autowired
    private ConversationCandidatRepository conversationCandidatRepository;

    @Autowired
    private MessageCandidatToCandidatRepository messageCandidatToCandidatRepository;

    @Transactional
    public ConversationCandidat trouverOuCreerConversation(Long candidat1Id, Long candidat2Id) {
        // Vérifier si une conversation existe déjà (dans les deux sens)
        ConversationCandidat conversation = conversationCandidatRepository.findByCandidats(candidat1Id, candidat2Id);
        
        if (conversation == null) {
            // Créer une nouvelle conversation avec les IDs dans l'ordre croissant
            Long idMin = Math.min(candidat1Id, candidat2Id);
            Long idMax = Math.max(candidat1Id, candidat2Id);
            
            conversation = new ConversationCandidat();
            conversation.setCandidat1Id(idMin);
            conversation.setCandidat2Id(idMax);
            conversation.setDateCreation(LocalDateTime.now());
            conversation.setDerniereMiseAJour(LocalDateTime.now());
            conversation.setStatut("ACTIVE");
            
            conversation = conversationCandidatRepository.save(conversation);
        }
        
        return conversation;
    }

    @Transactional
    public MessageCandidatToCandidat envoyerMessage(Long expediteurId, Long destinataireId, String contenu) {
        ConversationCandidat conversation = trouverOuCreerConversation(expediteurId, destinataireId);
        
        MessageCandidatToCandidat message = new MessageCandidatToCandidat();
        message.setExpediteurId(expediteurId);
        message.setDestinataireId(destinataireId);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDate.now());
        message.setEstLu(false);
        message.setConversation(conversation);
        
        message = messageCandidatToCandidatRepository.save(message);
        
        // Mettre à jour la date de dernière mise à jour de la conversation
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationCandidatRepository.save(conversation);
        
        return message;
    }

    public Page<ConversationCandidat> getConversationsCandidat(Long candidatId, Pageable pageable) {
        return conversationCandidatRepository.findByCandidatId(candidatId, pageable);
    }

    public Optional<ConversationCandidat> getConversationAvecMessages(Long conversationId) {
        return conversationCandidatRepository.findById(conversationId);
    }

    @Transactional
    public void marquerMessagesLus(Long conversationId, Long destinataireId) {
        List<MessageCandidatToCandidat> messagesNonLus = messageCandidatToCandidatRepository
            .findByConversationIdAndDestinataireIdAndEstLuFalse(conversationId, destinataireId);
        
        messagesNonLus.forEach(message -> message.setEstLu(true));
        messageCandidatToCandidatRepository.saveAll(messagesNonLus);
    }

    @Transactional
    public void archiverConversation(Long conversationId) {
        conversationCandidatRepository.findById(conversationId).ifPresent(conversation -> {
            conversation.setStatut("ARCHIVEE");
            conversationCandidatRepository.save(conversation);
        });
    }
}