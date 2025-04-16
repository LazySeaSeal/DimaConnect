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
        ConversationCandidat conversation = conversationCandidatRepository.findByCandidat1IdAndCandidat2Id(candidat1Id, candidat2Id);
        
        if (conversation == null) {
            conversation = new ConversationCandidat(candidat1Id, candidat2Id);
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
        
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationCandidatRepository.save(conversation);
        
        return message;
    }

    public Page<ConversationCandidat> getConversationsCandidat(Long candidatId, Pageable pageable) {
        return conversationCandidatRepository.findByCandidat1IdOrCandidat2Id(candidatId, candidatId, pageable);
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