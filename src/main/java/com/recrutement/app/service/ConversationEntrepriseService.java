package com.recrutement.app.service;

import com.recrutement.app.model.ConversationEntreprise;
import com.recrutement.app.model.MessageEntrepriseToEntreprise;
import com.recrutement.app.repository.ConversationEntrepriseRepository;
import com.recrutement.app.repository.MessageEntrepriseToEntrepriseRepository;
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
public class ConversationEntrepriseService {

    @Autowired
    private ConversationEntrepriseRepository conversationEntrepriseRepository;

    @Autowired
    private MessageEntrepriseToEntrepriseRepository messageEntrepriseToEntrepriseRepository;

    @Transactional
    public ConversationEntreprise trouverOuCreerConversation(Long entreprise1Id, Long entreprise2Id) {
        ConversationEntreprise conversation = conversationEntrepriseRepository.findByEntreprise1IdAndEntreprise2Id(entreprise1Id, entreprise2Id);
        
        if (conversation == null) {
            conversation = new ConversationEntreprise(entreprise1Id, entreprise2Id);
            conversation.setDateCreation(LocalDateTime.now());
            conversation.setDerniereMiseAJour(LocalDateTime.now());
            conversation.setStatut("ACTIVE");
            conversation = conversationEntrepriseRepository.save(conversation);
        }
        
        return conversation;
    }

    @Transactional
    public MessageEntrepriseToEntreprise envoyerMessage(Long expediteurId, Long destinataireId, String contenu) {
        ConversationEntreprise conversation = trouverOuCreerConversation(expediteurId, destinataireId);
        
        MessageEntrepriseToEntreprise message = new MessageEntrepriseToEntreprise();
        message.setExpediteurId(expediteurId);
        message.setDestinataireId(destinataireId);
        message.setContenu(contenu);
        message.setDateEnvoi(LocalDate.now());
        message.setEstLu(false);
        message.setConversation(conversation);
        message = messageEntrepriseToEntrepriseRepository.save(message);
        
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationEntrepriseRepository.save(conversation);
        
        return message;
    }

    public Page<ConversationEntreprise> getConversationsEntreprise(Long entrepriseId, Pageable pageable) {
        return conversationEntrepriseRepository.findByEntreprise1IdOrEntreprise2Id(entrepriseId, entrepriseId, pageable);
    }

    public Optional<ConversationEntreprise> getConversationAvecMessages(Long conversationId) {
        return conversationEntrepriseRepository.findById(conversationId);
    }

    @Transactional
    public void marquerMessagesLus(Long conversationId, Long destinataireId) {
        List<MessageEntrepriseToEntreprise> messagesNonLus = messageEntrepriseToEntrepriseRepository
            .findByConversationIdAndDestinataireIdAndEstLuFalse(conversationId, destinataireId);
        
        messagesNonLus.forEach(message -> message.setEstLu(true));
        messageEntrepriseToEntrepriseRepository.saveAll(messagesNonLus);
    }

    @Transactional
    public void archiverConversation(Long conversationId) {
        conversationEntrepriseRepository.findById(conversationId).ifPresent(conversation -> {
            conversation.setStatut("ARCHIVEE");
            conversationEntrepriseRepository.save(conversation);
        });
    }
}