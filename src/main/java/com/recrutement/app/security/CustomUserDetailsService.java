package com.recrutement.app.security;

import com.recrutement.app.model.Employe;
import com.recrutement.app.model.Entreprise;
import com.recrutement.app.repository.EmployeRepository;
import com.recrutement.app.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Format attendu: "type:email" où type est soit "employe" soit "entreprise"
        String[] parts = username.split(":", 2);

        if (parts.length != 2) {
            throw new UsernameNotFoundException("Format d'identifiant invalide");
        }

        String type = parts[0];
        String email = parts[1];

        if ("employe".equals(type)) {
            return loadEmploye(email);
        } else if ("entreprise".equals(type)) {
            return loadEntreprise(email);
        } else {
            throw new UsernameNotFoundException("Type d'utilisateur inconnu: " + type);
        }
    }

    private UserDetails loadEmploye(String email) {
        Employe employe = employeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employé non trouvé avec l'email: " + email));

        // Créer une autorité basée sur le rôle de l'employé
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + employe.getRole().name());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new User(
                employe.getId().toString(),  // Utiliser l'ID comme username pour Spring Security
                employe.getMotDePasse(),
                authorities
        );
    }

    private UserDetails loadEntreprise(String email) {
        Entreprise entreprise = entrepriseRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Entreprise non trouvée avec l'email: " + email));

        // Pour les entreprises, on attribue un rôle standard
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ENTREPRISE");
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new User(
                entreprise.getId().toString(),  // Utiliser l'ID comme username pour Spring Security
                entreprise.getMotDePasse(),
                authorities
        );
    }
}