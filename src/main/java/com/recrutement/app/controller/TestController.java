package com.recrutement.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Contenu public";
    }
    
    @GetMapping("/candidat")
    @PreAuthorize("hasAuthority('ROLE_CANDIDAT')")
    public String candidatAccess() {
        return "Contenu réservé aux candidats";
    }
    
    @GetMapping("/entreprise")
    @PreAuthorize("hasAuthority('ROLE_ENTREPRISE')")
    public String entrepriseAccess() {
        return "Contenu réservé aux entreprises";
    }
}
