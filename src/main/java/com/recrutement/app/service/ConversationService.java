package com.recrutement.app.service;

import com.recrutement.app.model.Conversation;
import com.recrutement.app.model.MessageCandidat;
import com.recrutement.app.model.MessageEntreprise;
import com.recrutement.app.repository.ConversationRepository;
import com.recrutement.app.repository.MessageCandidatRepository;
import com.recrutement.app.repository.MessageEntrepriseRepository;
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
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageCandidatRepository messageCandidatRepository;

    @Autowired
    private MessageEntrepriseRepository messageEntrepriseRepository;

    @Transactional
    public Conversation trouverOuCreerConversation(Long candidatId, Long entrepriseId) {
        Conversation conversation = conversationRepository.findByCandidatIdAndEntrepriseId(candidatId, entrepriseId);
        
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setCandidatId(candidatId);
            conversation.setEntrepriseId(entrepriseId);
            conversation.setDateCreation(LocalDateTime.now());
            conversation.setDerniereMiseAJour(LocalDateTime.now());
            conversation.setStatut("ACTIVE");
            conversation = conversationRepository.save(conversation);
        }
        
        return conversation;
    }

    @Transactional
    public MessageCandidat envoyerMessageCandidat(Long candidatId, Long entrepriseId, String contenu) {
        Conversation conversation = trouverOuCreerConversation(candidatId, entrepriseId);
        
        MessageCandidat message = new MessageCandidat();
        message.setCandidatId(candidatId);
        message.setEntrepriseId(entrepriseId);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDate.now());
        message.setEstLu(false);
        message.setConversation(conversation);
        message = messageCandidatRepository.save(message);
        
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return message;
    }

    @Transactional
    public MessageEntreprise envoyerMessageEntreprise(Long entrepriseId, Long candidatId, String contenu) {
        Conversation conversation = trouverOuCreerConversation(candidatId, entrepriseId);
        
        MessageEntreprise message = new MessageEntreprise();
        message.setCandidatId(candidatId);
        message.setEntrepriseId(entrepriseId);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDate.now());
        message.setEstLu(false);
        message.setConversation(conversation);
        message = messageEntrepriseRepository.save(message);
        
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return message;
    }

    public Page<Conversation> getConversationsCandidat(Long candidatId, Pageable pageable) {
        return conversationRepository.findByCandidatId(candidatId, pageable);
    }

    public Page<Conversation> getConversationsEntreprise(Long entrepriseId, Pageable pageable) {
        return conversationRepository.findByEntrepriseId(entrepriseId, pageable);
    }

    public Optional<Conversation> getConversationAvecMessages(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    @Transactional
    public void marquerMessagesLusPourEntreprise(Long conversationId, Long entrepriseId) {
        List<MessageCandidat> messagesNonLus = messageCandidatRepository
            .findByConversationIdAndEntrepriseIdAndEstLuFalse(conversationId, entrepriseId);
        
        messagesNonLus.forEach(message -> message.setEstLu(true));
        messageCandidatRepository.saveAll(messagesNonLus);
    }

    @Transactional
    public void marquerMessagesLusPourCandidat(Long conversationId, Long candidatId) {
        List<MessageEntreprise> messagesNonLus = messageEntrepriseRepository
            .findByConversationIdAndCandidatIdAndEstLuFalse(conversationId, candidatId);
        
        messagesNonLus.forEach(message -> message.setEstLu(true));
        messageEntrepriseRepository.saveAll(messagesNonLus);
    }

    @Transactional
    public void archiverConversation(Long conversationId) {
        conversationRepository.findById(conversationId).ifPresent(conversation -> {
            conversation.setStatut("ARCHIVEE");
            conversationRepository.save(conversation);
        });
    }
}