package com.recrutement.app.config.security;

/**
 * Constantes pour les rôles utilisées dans Spring Security.
 * Ce n'est pas une entité persistante, juste des constantes.
 */
public class Role {
    public static final String ROLE_CANDIDAT = "ROLE_CANDIDAT";
    public static final String ROLE_ENTREPRISE = "ROLE_ENTREPRISE";
    
    // Empêcher l'instanciation
    private Role() {}
}
