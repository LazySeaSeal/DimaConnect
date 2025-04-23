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
        // Vérifier si une conversation existe déjà (dans les deux sens)
        ConversationEntreprise conversation = conversationEntrepriseRepository.findByEntreprises(entreprise1Id, entreprise2Id);
        
        if (conversation == null) {
            // Créer une nouvelle conversation avec les IDs dans l'ordre croissant
            Long idMin = Math.min(entreprise1Id, entreprise2Id);
            Long idMax = Math.max(entreprise1Id, entreprise2Id);
            
            conversation = new ConversationEntreprise();
            conversation.setEntreprise1Id(idMin);
            conversation.setEntreprise2Id(idMax);
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
        
        // Mettre à jour la date de dernière mise à jour de la conversation
        conversation.setDerniereMiseAJour(LocalDateTime.now());
        conversationEntrepriseRepository.save(conversation);
        
        return message;
    }

    public Page<ConversationEntreprise> getConversationsEntreprise(Long entrepriseId, Pageable pageable) {
        return conversationEntrepriseRepository.findByEntrepriseId(entrepriseId, pageable);
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