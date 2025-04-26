package com.recrutement.app.config.security;

import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.CandidatRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Vérifier d'abord si c'est un candidat
        Candidat candidat = candidatRepository.findByEmail(email).orElse(null);
        if (candidat != null) {
            return UserDetailsImpl.buildFromCandidat(candidat);
        }

        // Sinon, vérifier si c'est une entreprise
        Entreprise entreprise = entrepriseRepository.findByEmail(email).orElse(null);
        if (entreprise != null) {
            return UserDetailsImpl.buildFromEntreprise(entreprise);
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
    }
}
